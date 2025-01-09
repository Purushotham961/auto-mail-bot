package org.purushotham.tests;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.purushotham.testcomponents.BaseComponent;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

public class TestMailingFeatures extends BaseComponent {

    @Test
    public void testSendMail() {

        try (FileInputStream fis = new FileInputStream(
                System.getProperty("user.dir") + "/src/main/resources/contacts.xlsx")
        ) {
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                String sheetName = workbook.getSheetName(i);
                System.out.println("Sheet name : " + sheetName);

                //Pass the sheet name where the data is present
                if (sheetName.equalsIgnoreCase("Table 1")) {
                    XSSFSheet sheet = workbook.getSheetAt(i);
                    Iterator<Row> rows = sheet.iterator();
                    Row firstRow = rows.next();
                    Iterator<Cell> cellIterator = firstRow.cellIterator();
                    String recipientName = null;
                    String recipientEmail = null;
                    String recipientTitle = null;
                    String recipientCompany = null;
                    String sNo = null;

                    int k = 0;
                    int recipientNameIndex = 0;
                    int recipientEmailIndex = 0;
                    int recipientTitleIndex = 0;
                    int recipientCompanyIndex = 0;
                    int sNoIndex = 0;
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        String cellValue = cell.getStringCellValue();
                        if (cellValue.contains("Email")) {
                            recipientEmail = cell.getStringCellValue();
                            recipientEmailIndex = k;
                        } else if (cellValue.contains("Title")) {
                            recipientTitle = cell.getStringCellValue();
                            recipientTitleIndex = k;
                        } else if (cellValue.contains("Company")) {
                            recipientCompany = cell.getStringCellValue();
                            recipientCompanyIndex = k;
                        } else if (cellValue.contains("SNo")) {
                            sNo = cell.getStringCellValue();
                            sNoIndex = k;
                        } else if (cellValue.contains("Name")) {
                            recipientName = cell.getStringCellValue();
                            recipientNameIndex = k;
                        }
                        k++;
                    }
                    System.out.println(sNo + " : " + sNoIndex);
                    System.out.println(recipientEmail + " : " + recipientEmailIndex);
                    System.out.println(recipientTitle + " : " + recipientTitleIndex);
                    System.out.println(recipientCompany + " : " + recipientCompanyIndex);
                    System.out.println(recipientName + " : " + recipientNameIndex);

                    while (rows.hasNext()) {
                        Row row = rows.next();
                        double sNoValue = row.getCell(sNoIndex).getNumericCellValue();
                        recipientEmail = row.getCell(recipientEmailIndex).getStringCellValue().trim();
                        recipientCompany = row.getCell(recipientCompanyIndex).getStringCellValue().trim();
                        recipientTitle = row.getCell(recipientTitleIndex).getStringCellValue().trim();
                        recipientName = row.getCell(recipientNameIndex).getStringCellValue().trim();

                        System.out.println((int) sNoValue + " - " + recipientName + " - " + recipientTitle + " - " + recipientEmail + " - " + recipientCompany);

                        String subject = "Subject of the email";
                        String message = "Message body of the email";

                        try {
                            driver.findElement(By.xpath("//div[text()='Compose']")).click();
                            landingPage.waitForWebElementToAppear(driver.findElement(By.xpath("//div[@role='dialog']")));
                            WebElement composeDialog = driver.findElement(By.xpath("//div[@role='dialog']"));
                            composeDialog.findElement(By.xpath("//img[@aria-label='Full screen (Shift for pop-out)']")).click();
                            Thread.sleep(2000);

                            //enter the Recipient recipientName to whom you want to send the mail
                            try {
                                driver.findElement(By.cssSelector("[aria-label='To recipients']")).sendKeys(recipientEmail);
                            } catch (ElementNotInteractableException e) {
                                Thread.sleep(2000);
                                landingPage.clearAndEnterText(driver.findElement(By.cssSelector("[aria-label='To recipients']")), recipientEmail);
                            }

                            //Enter the subject of the recipientEmail
                            driver.findElement(By.cssSelector("[placeholder='Subject']")).sendKeys(subject);

                            //Enter the message
                            WebElement messageBody = driver.findElement(By.xpath("//div[@aria-label='Message Body']"));
                            landingPage.clearAndEnterText(messageBody, message);

                            Thread.sleep(2000L);

                            //Attach the files
                            String resumePath = System.getProperty("user.dir") + "/src/main/resources/example.pdf";
                            driver.findElement(By.cssSelector("[name='Filedata']")).sendKeys(resumePath);
                            Thread.sleep(1000L);
                            landingPage.waitForWebElementToDisappear(driver.findElement(By.cssSelector("[aria-label*='Uploading']")));

                            //Send the mail
                            messageBody.sendKeys(Keys.chord(Keys.CONTROL, Keys.ENTER));
                        } catch (Exception e1) {
                            //check if compose dialog is displayed
                            boolean isCloseButtonDisplayed = driver.findElement(By.cssSelector("[alt='Close']")).isDisplayed();
                            System.out.println("Is close button displayed: "+ isCloseButtonDisplayed);
                            System.out.println("Exception occurred while sending mail to : " + recipientEmail);
                            System.out.println(e1.getMessage());
                            if (isCloseButtonDisplayed) {
                                try {
                                    //close the compose dialog
                                    driver.findElement(By.cssSelector("[alt='Close']")).click();
                                } catch (ElementClickInterceptedException e2) {
                                    //If no user found with the provided mail id, a popup will be shown, handle that popup
                                    driver.findElement(By.xpath("//span[text()='OK']/ancestor::button")).click();

                                    //close the compose dialog
                                    driver.findElement(By.cssSelector("[alt='Close']")).click();
                                }
                            }
                        }

                    }

                }

            }
        } catch (IOException e) {
            throw new IllegalStateException();
        }
    }
}
