package com.threehero.zuixianlou.controller;

import com.threehero.zuixianlou.common.R;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/common")
public class CommonController {

  @Value("${zuixianlou.path}")
  private String basePath;

  /**
   * 文件上传
   * @param file
   * @return
   */
  @PostMapping("/upload")
  public R<String> upload(MultipartFile file) {

    // 获取原始文件名
    String originalFileName = file.getOriginalFilename();

    assert originalFileName != null; // 断言
    String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));

    // 使用 uuid 重新生成文件名
    String fileName = UUID.randomUUID().toString() + suffix;

    // 创建一个目录对象
    File dir = new File(basePath);
    // 判断当前目录是否存在
    if (!dir.exists()) {
      // 创建
      dir.mkdirs();
    }

    try {
      // 将临时文件转存到指定位置
      file.transferTo(new File(basePath + fileName));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return R.success(fileName);
  }

  /**
   * 文件下载
   * @param name
   * @param response
   */
  @GetMapping("/download")
  public void download(String name, HttpServletResponse response) {
    System.out.println(name);
    try {
      FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
      ServletOutputStream outputStream = response.getOutputStream();
      response.setContentType("image/jpeg");
      byte[] bytes = new byte[1024];
      int len = 0;
      while ((len = fileInputStream.read(bytes)) != -1) {
        outputStream.write(bytes, 0, len);
        outputStream.flush();
      }
      // 关闭资源
      outputStream.close();
      fileInputStream.close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
