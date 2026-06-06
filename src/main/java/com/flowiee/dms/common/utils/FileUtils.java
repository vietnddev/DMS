package com.flowiee.dms.common.utils;

import com.flowiee.dms.common.StartUp;
import com.flowiee.dms.storage.entity.FileStorage;
import com.flowiee.dms.common.exception.AppException;
import com.flowiee.dms.account.model.MODULE;
import com.flowiee.dms.common.utils.constants.FileExtension;
import com.flowiee.dms.storage.model.FolderTree;
import com.flowiee.dms.storage.dto.FileDTO;
import com.flowiee.dms.common.utils.constants.SystemPath;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileUtils {
    private static final Logger mvLogger = LoggerFactory.getLogger(FileUtils.class);
    public static String templatePath = "src/main/resources/static";
    public static String fileUploadPath = StartUp.getResourceUploadPath() + File.separator + "uploads" + File.separator;
    public static String fileDownloadPath = StartUp.getResourceUploadPath() + File.separator + "downloads" + File.separator;
    public static String excelTemplatePath = templatePath + File.separator + "templates/excel";
    // Bản đồ dùng để lưu trữ khóa file theo đường dẫn
    private static final ConcurrentHashMap<String, FileLock> mvFileLocks = new ConcurrentHashMap<>();

    public static void createCellCombobox(XSSFWorkbook workbook, XSSFSheet sheet, XSSFSheet hsheet, List<String> listValue, int row, int column, String nameName) {
        //Put các tên danh mục vào column trong sheet danh mục ẩn
        for (int i = 0; i < listValue.size(); i++) {
            XSSFRow hideRow = hsheet.getRow(i);
            if (hideRow == null) {
                hideRow = hsheet.createRow(i);
            }
            hideRow.createCell(column).setCellValue(listValue.get(i));
        }

        // Khởi tạo name cho mỗi loại danh mục
        Name namedRange = workbook.createName();
        namedRange.setNameName(nameName);
        String colName = CellReference.convertNumToColString(column);
        namedRange.setRefersToFormula(hsheet.getSheetName() + "!$" + colName + "$1:$" + colName + "$" + listValue.size());

        sheet.autoSizeColumn(column); //Auto điều chỉnh độ rộng cột

        DataValidationHelper validationHelper = new XSSFDataValidationHelper(sheet);
        CellRangeAddressList addressList = new CellRangeAddressList(row, row, column, column); //Tạo dropdownlist cho một cell
        DataValidationConstraint constraint = validationHelper.createFormulaListConstraint(nameName);
        DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);

        dataValidation.setSuppressDropDownArrow(true); //Hiển thị mũi tên xổ xuống để chọn giá trị
        dataValidation.setShowErrorBox(true); //Hiển thị hộp thoại lỗi khi chọn giá trị không hợp lệ
        dataValidation.createErrorBox("Error", "Giá trị đã chọn không hợp lệ!");
        dataValidation.setEmptyCellAllowed(false); //Không cho phép ô trống trong dropdownlist
        dataValidation.setShowPromptBox(true); //Hiển thị hộp nhắc nhở khi người dùng chọn ô
        dataValidation.createPromptBox("Danh mục hệ thống", "Vui lòng chọn giá trị!"); //Tạo hộp thoại nhắc nhở khi click chuột vào cell

        sheet.addValidationData(dataValidation);
    }

    public static String getFileExtension(String fileName) {
        String extension = "";
        if (ObjectUtils.isNotEmpty(fileName)) {
            int lastIndex = fileName.lastIndexOf('.');
            if (lastIndex > 0 && lastIndex < fileName.length() - 1) {
                extension = fileName.substring(lastIndex + 1);
            }
        }
        return extension;
    }

    public static boolean isAllowUpload(String fileExtension, boolean throwException, String message) {
        if (ObjectUtils.isNotEmpty(fileExtension)) {
            for (FileExtension ext : FileExtension.values()) {
                if (ext.key().equalsIgnoreCase(fileExtension) && ext.isAllowUpload()) {
                    return true;
                }
            }
        }
        if (throwException) {
            throw new AppException(String.format(message != null ? message : "File có định dạng .%s chưa được hỗ trợ!", fileExtension));
        }
        return false;
    }

    public static File getFileUploaded(FileStorage fileModel) {
        Path path = Paths.get(StartUp.getResourceUploadPath() + File.separator + fileModel.getDirectoryPath() + File.separator + fileModel.getStorageName());
        return new File(path.toUri());
    }

    public static Path getSystemPath(SystemPath systemPath) {
        return switch (systemPath) {
            case TemplateExportTemp -> Path.of(excelTemplatePath + "/temp");
            case DownloadStorageTemp -> Path.of(fileDownloadPath + "/storage/temp");
            case ImportStorageTemp -> Path.of(fileUploadPath + "/temp");
            case InitDataCsv -> Paths.get(templatePath + File.separator + "data/csv/Category.csv");
            case Report -> Paths.get(templatePath + File.separator + "report");
            case Logo -> Paths.get(StartUp.getResourceUploadPath() + File.separator + "dist/img/FlowieeLogo.png");
            default -> throw new IllegalStateException("Unexpected value: " + systemPath);
        };
    }

    public static boolean lockFile(File pFile) {
        if (mvFileLocks.get(pFile.getAbsolutePath()) != null) {
            mvLogger.info("File is locking!");
            return true;
        }
        try {
            // Mở file và tạo khóa cho nó
            RandomAccessFile raf = new RandomAccessFile(pFile, "rw");
            FileChannel channel = raf.getChannel();
            FileLock lock = channel.lock();
            // Lưu trữ khóa để sử dụng trong unlockFile
            mvFileLocks.put(pFile.getAbsolutePath(), lock);
            mvLogger.info("File locked: " + pFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            mvLogger.error("Cannot lock the file, it may be in use or another error has occurred.: " + pFile.getAbsolutePath());
            return false;
        }
    }

    public static boolean unlockFile(File pFile) {
        String lvFilePath = pFile.getAbsolutePath();
        FileLock lock = mvFileLocks.get(lvFilePath);
        if (lock != null) {
            try {
                lock.release();// Mở khóa pFile
                lock.channel().close();
                mvFileLocks.remove(lvFilePath);
                mvLogger.info("File unlocked: " + lvFilePath);
                return true;
            } catch (IOException e) {
                mvLogger.error("File cannot unlock: " + e.getMessage());
                return false;
            }
        } else {
            mvLogger.info("Cannot unlock because the file is not locked.");
            return false;
        }
    }

    public static void addFileToDirectory(String filePath, String directoryPath, StandardCopyOption copyOption) {
        Path sourcePath = Paths.get(filePath);
        Path targetPath = Paths.get(directoryPath, sourcePath.getFileName().toString());
        try {
            if (!Files.exists(sourcePath) || !Files.exists(targetPath)) {
                return;
            }
            Files.copy(sourcePath, targetPath, copyOption);
            System.out.println("File " + filePath + " copied to " + directoryPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void zipDirectory(String sourceFolderPath, String zipFilePath) {
        Path sourcePath = Paths.get(sourceFolderPath);
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(Paths.get(zipFilePath)))) {
            Files.walkFileTree(sourcePath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    ZipEntry zipEntry = new ZipEntry(sourcePath.relativize(file).toString().replace("\\", "/"));
                    zos.putNextEntry(zipEntry);
                    Files.copy(file, zos);
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (!sourcePath.equals(dir)) {
                        ZipEntry zipEntry = new ZipEntry(sourcePath.relativize(dir).toString().replace("\\", "/") + "/");
                        zos.putNextEntry(zipEntry);
                        zos.closeEntry();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
            System.out.println("Directory successfully compressed to " + zipFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteDirectory(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    // Hàm đệ quy để xây dựng cây thư mục
    public static FolderTree buildFolderTree(File folder, int level, long parentId, String parentName) {
        FolderTree folderTree = FolderTree.builder()
                .name(folder.getName())
                .isDirectory(folder.isDirectory())
                .level(level)
                .parentId(parentId)
                .parentName(parentName)
                .subFiles(new ArrayList<>()).build();

        // Lấy danh sách tất cả các file và thư mục con
        File[] subFiles = folder.listFiles();
        if (subFiles != null) {
            for (File file : subFiles) {
                if (file.isDirectory()) {
                    // Gọi đệ quy cho thư mục con
                    folderTree.getSubFiles().add(buildFolderTree(file, level + 1, parentId, folder.getName()));
                } else {
                    isAllowUpload(FileUtils.getFileExtension(file.getName()), true, "Tồn tại tệp có định dạng .%s chưa được hỗ trợ!");
                    // Thêm file vào danh sách subFiles (ở đây chỉ thêm file, không gọi đệ quy)
                    folderTree.getSubFiles().add(FolderTree.builder()
                            .name(file.getName())
                            .isDirectory(file.isDirectory())
                            .level(level + 1)
                            .parentId(parentId)
                            .parentName(folder.getName())
                            .file(file)
                            .build());
                }
            }
        }
        return folderTree;
    }

    public static File unzipDirectory(File pFileZip, String pDestDir) throws IOException {
        String lvDestDir = pDestDir;
        if (lvDestDir == null) {
            String pathNotIncludeFileExtension = removeFileExtension(pFileZip.getAbsolutePath());
            lvDestDir = pathNotIncludeFileExtension.substring(0, pathNotIncludeFileExtension.lastIndexOf('\\'));
        }
        File destDirectory = new File(lvDestDir);

        try {
            byte[] buffer = new byte[1024];
            ZipInputStream zis = new ZipInputStream(new FileInputStream(pFileZip));
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(destDirectory, zipEntry);
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    // fix for Windows-created archives
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }

                    // write file content
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                zipEntry = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (pDestDir == null) {
            return new File(removeFileExtension(pFileZip.getAbsolutePath()));
        } else {
            return destDirectory;
        }
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    public static String removeFileExtension(String originalFilename) {
        String fileExtension = FileUtils.getFileExtension(originalFilename);
        return originalFilename.replaceAll("." + fileExtension, "");
    }

    public static MultipartFile convertFileToMultipartFile(File file) throws IOException {
        // Đọc dữ liệu từ file
        FileInputStream inputStream = new FileInputStream(file);

        // Tạo đối tượng MockMultipartFile từ File
        MultipartFile multipartFile = new MockMultipartFile(
                file.getName(), // Tên của file
                file.getName(), // Tên gốc của file
                Files.probeContentType(file.toPath()), // "application/octet-stream", // Loại MIME (hoặc có thể là bất kỳ loại nào bạn muốn)
                inputStream); // Dữ liệu của file

        return multipartFile;
    }

    public static String getFileUploadPath() {
        if (StartUp.getResourceUploadPath() == null) {
            throw new AppException("The uploaded file saving directory is not configured, please try again later!");
        }
        return StartUp.getResourceUploadPath() + "/uploads/";
    }

    public static long getFolderSize(File folder) {
        long length = 0;

        // Lấy danh sách tất cả các file và thư mục con
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    // Nếu là file, lấy dung lượng của file
                    length += file.length();
                } else {
                    // Nếu là thư mục, gọi đệ quy để lấy dung lượng thư mục con
                    length += getFolderSize(file);
                }
            }
        }

        return length;
    }

    public static List<FileDTO> getDocumentFiles(FileStorage pFileModel) {
        File lvFileUploaded = getFileUploaded(pFileModel);
        if (!lvFileUploaded.exists())
            return List.of();

        List<FileDTO> lvFileList = new ArrayList<>();
        lvFileList.add(FileDTO.builder()
                .id(pFileModel.getId())
                .file(lvFileUploaded)
                .build());

        String extension = "." + pFileModel.getExtension();
        if (FileExtension.DOC.key().equals(pFileModel.getExtension()) || FileExtension.DOCX.key().equals(pFileModel.getExtension()) ||
                FileExtension.XLS.key().equals(pFileModel.getExtension()) || FileExtension.XLSX.key().equals(pFileModel.getExtension()))
        {
            //pdf
            FileStorage pdfModel = ObjectUtils.clone(pFileModel);
            pdfModel.setStorageName(pdfModel.getStorageName().replaceAll(extension, ".pdf"));
            File lvFilePdf = FileUtils.getFileUploaded(pdfModel);
            if (lvFilePdf.exists()) {
                lvFileList.add(FileDTO.builder()
                        .id(pFileModel.getId())
                        .file(lvFilePdf)
                        .build());
            }
            //png
            FileStorage imageModel = ObjectUtils.clone(pFileModel);
            imageModel.setStorageName(imageModel.getStorageName().replaceAll(extension, ".png"));
            File lvFileImage = FileUtils.getFileUploaded(imageModel);
            if (lvFileImage.exists()) {
                lvFileList.add(FileDTO.builder()
                        .id(pFileModel.getId())
                        .file(lvFileImage)
                        .build());
            }
            //others file type
        }

        return lvFileList;
    }

    public static String generateAliasName(String text) {
        String transformedText = "";
        if (text != null) {
            // Loại bỏ dấu tiếng Việt và ký tự đặc biệt
            String normalizedText = Normalizer.normalize(text, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            String textWithoutAccents = pattern.matcher(normalizedText).replaceAll("");
            String cleanedText = textWithoutAccents.replaceAll("[^a-zA-Z0-9 ]", "");

            // Chuyển đổi thành chữ thường (lowercase)
            String lowercaseText = cleanedText.toLowerCase();

            // Thay thế khoảng trắng bằng dấu gạch ngang ("-")
            transformedText = lowercaseText.replace(" ", "-");

            if (transformedText.endsWith("-")) {
                transformedText = transformedText.substring(0, transformedText.length() - 1);
            }
        }
        return transformedText;
    }

    public static String getUploadPathDir(String systemModule) {
        try {
            StringBuilder path = new StringBuilder(FileUtils.getFileUploadPath());
            if (MODULE.STORAGE.name().equals(systemModule)) {
                path.append("storage");
            } else if (MODULE.CATEGORY.name().equals(systemModule)) {
                path.append("category");
            }
            path.append("/").append(LocalDateTime.now().getYear());
            path.append("/").append(LocalDateTime.now().getMonth().getValue());
            path.append("/").append(LocalDateTime.now().getDayOfMonth());
            File folder = new File(path.toString());
            if (!folder.exists()) {
                if (folder.mkdirs()) {
                    System.out.println("mkdir OK");
                }
            }
            return path.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}