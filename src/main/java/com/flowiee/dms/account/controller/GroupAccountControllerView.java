package com.flowiee.dms.account.controller;

import com.flowiee.dms.common.web.BaseController;
import com.flowiee.dms.account.entity.GroupAccount;
import com.flowiee.dms.common.exception.ResourceNotFoundException;
import com.flowiee.dms.account.service.GroupAccountService;
import com.flowiee.dms.common.utils.PagesUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@RestController
@RequestMapping("/sys/group-account")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GroupAccountControllerView extends BaseController {
    GroupAccountService groupAccountService;

    @GetMapping
    @PreAuthorize("@vldModuleSystem.readGroupAccount(true)")
    public ModelAndView findAllGroup() {
        return baseView(new ModelAndView(PagesUtils.SYS_GR_ACC));
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("@vldModuleSystem.readGroupAccount(true)")
    public ModelAndView findDetailGroup(@PathVariable("id") Long groupId) {
        Optional<GroupAccount> groupAcc = groupAccountService.findById(groupId);
        if (groupAcc.isEmpty()) {
            throw new ResourceNotFoundException("Group account not found!", true);
        }
        ModelAndView modelAndView = new ModelAndView(PagesUtils.SYS_GR_ACC_DETAIL);
        modelAndView.addObject("groupAccount", groupAcc.get());
        return baseView(modelAndView);
    }
}