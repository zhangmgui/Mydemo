package com.xytest.DataGrabbing;

import net.sourceforge.tess4j.Tesseract;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangmg on 2017/5/2.
 */
public class Selenium {
    @Test
    public void openChrome() throws Exception {
        System.out.println(Selenium.class.getResource("/chromedriver.exe").getFile());
        System.setProperty("webdriver.chrome.driver", Selenium.class.getResource("/chromedriver.exe").getFile());

        WebDriver driver = new ChromeDriver();
        driver.get("https://www.baidu.com/");

        WebElement u1 = driver.findElement(By.id("u1"));
        System.out.println(u1.toString());
        WebElement tj_login = u1.findElement(By.className("lb"));
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        tj_login.click();
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        WebElement elemUsername = driver.findElement(By.id("TANGRAM__PSP_8__userName"));
        WebElement elemPassword = driver.findElement(By.id("TANGRAM__PSP_8__password"));
        WebElement btn = driver.findElement(By.id("TANGRAM__PSP_8__submit"));
        WebElement rememberMe = driver.findElement(By.id("TANGRAM__PSP_8__memberPass"));

        // 操作页面元素
        elemUsername.sendKeys("女神之泪ol");
        elemPassword.sendKeys("zhangmg321");
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
       //输入密码后等1秒钟，让图片生成
        WebElement image = driver.findElement(By.id("TANGRAM__PSP_8__verifyCodeImg"));
        String url = image.getAttribute("src");
        File file = getFileByUrl(url);
        Tesseract tesseract = new Tesseract();
        rememberMe.click();

        // 提交表单
        btn.submit();

    }

    public File getFileByUrl(String url) throws Exception {
        URL urlObj = new URL(url);
        URLConnection uc = urlObj.openConnection();
        uc.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");

        InputStream is = uc.getInputStream();
        File file = new File(Selenium.class.getResource("").getFile()+"/a.png");

        if(!file.exists()){
            file.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(Selenium.class.getResource("").getFile()+"/a.png");
        byte[] buff = new byte[1024];
        int length;
        while((length=is.read(buff))!=-1){
            fos.write(buff,0,length);
        }
        fos.close();
        is.close();

        return file;
    }
}
