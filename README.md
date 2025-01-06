# Auto-Mail-Bot  
A Selenium and Java-based automation script designed to send emails to multiple recipients seamlessly. This tool automates the email-sending process, offering dynamic recipient handling, customizable email content, and robust error management for failed deliveries. Ideal for bulk email tasks, notifications, and automated reporting.  

## Key Features  
- **Dynamic Recipient Handling** – Easily manage and update recipient lists.  
- **Customizable Email Content** – Personalize emails with dynamic placeholders.  
- **Error Handling** – Logs and retries failed email deliveries for maximum efficiency.  
- **Remote Debugging Support** – Run the script in an already opened browser session.  
- **Cross-Platform** – Compatible with Windows, macOS, and Linux.  

## Running the Script in an Existing Browser Session  

### Windows  
1. **Download Google Chrome Canary** – [Download Here](https://www.google.com/chrome/canary/)  
2. **Open Terminal in the Project Root Folder**  
3. **Run the Following Command:**  
   ```bash
   start "C:\path-to-chrome.exe" --remote-debugging-port=9222
