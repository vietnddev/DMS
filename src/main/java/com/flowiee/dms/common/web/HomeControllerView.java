package com.flowiee.dms.common.web;

import com.flowiee.dms.account.entity.Account;
import com.flowiee.dms.account.service.AccountService;
import com.flowiee.dms.notification.service.SendMailService;
import com.flowiee.dms.common.utils.PagesUtils;
import com.flowiee.dms.common.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HomeControllerView extends BaseController {
    private final AccountService  mvAccountService;
    private final SendMailService mvSendMailService;
    private final PasswordEncoder mvPasswordEncoder;

    @GetMapping(value = "/change-password")
    public ModelAndView showPageChangePassword() {
        return new ModelAndView(PagesUtils.SYS_UNAUTHORIZED);
    }

    @GetMapping(value = "/change-password", params = "submit")
    public void submitChangePassword(){
        //
    }

    @GetMapping(value = "/forgot-password")
    public void showPageForgotPassword(){
        //Send password to email register
    }

    @GetMapping(value = "/reset-password")
    public ModelAndView resetPassword(HttpSession session, HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
        String email = SecurityUtils.getCurrentUser().getEmail();
        if(!ObjectUtils.isEmpty(email)) {
            String resetToken = UUID.randomUUID().toString();
            log.info("Reset password ");
            log.info("Email: " + email);
            log.info("Token: " + resetToken);
            mvAccountService.updateTokenForResetPassword(email, resetToken);

            //URL Like This : http://localhost:8080/reset-password?token=dfjdlkfjsldfdlfkdflkdfjdlk
            String fullURL = request.getRequestURL().toString();
            String resetPwdURL = fullURL.replace(request.getServletPath(), "") + "/reset-password?token=" + resetToken;
            log.info("URL: " + resetPwdURL);

            String subject = "Password Reset for FLOWIEE account";
            String content = "<p>Hello, </p>" +
                    "<p>You have requested to reset your password. </p> " +
                    "<p>Please click the link to change your password:</p>"+
                    "<p><a href=\""+ resetPwdURL + "\">Change my password</a></p>";
            boolean isEmailSendToUser = mvSendMailService.sendMail(subject, email, content);

            if (isEmailSendToUser) {
                session.setAttribute("successMsg", "Please check your email, password reset link has been sent to your email.");
            } else {
                session.setAttribute("errorMsg", "Something wrong on server. Email Not Sent!");
            }
        } else {
            session.setAttribute("errorMsg", "Invalid Email");
        }
        return new ModelAndView("redirect:/forgot-password");
    }

    @PostMapping("/reset-password")
    public void resetPasswordOperation(@RequestParam String token, @RequestParam String password,
                                       HttpServletRequest request, HttpServletResponse response,
                                       HttpSession session, Model model) throws IOException {
        Account userByToken = mvAccountService.getUserByResetTokens(token);
        if (ObjectUtils.isEmpty(userByToken)) {
            model.addAttribute("msg", "Your Link is invalid or expired!");
            String currentUrl = request.getRequestURL().toString();
            response.sendRedirect(currentUrl);
        } else {
            String newPassword = mvPasswordEncoder.encode(password);
            userByToken.setPassword(newPassword);
            userByToken.setResetTokens(null);
            mvAccountService.resetPassword(userByToken);
            session.setAttribute("successMsg", "Password Changed Successfully");
            model.addAttribute("msg", "Password Changed Successfully");
            response.sendRedirect(request.getServletPath());
        }
    }
}