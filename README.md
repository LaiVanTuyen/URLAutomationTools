# URL Automation Tool

A Java-based application that automates processing multiple URLs simultaneously using Selenium WebDriver.

## Features

- Process multiple URLs concurrently with multi-threading (up to 7 simultaneous browsers)
- Modern GUI interface with multiple themes (Modern Blue, Dark Mode, Light Mode)
- Automated login capability for web pages
- Progress tracking with visual indicators
- Ability to import URLs from text files
- Detailed status reporting and error handling
- Embedded ChromeDriver for cross-platform compatibility

## Technical Implementation

- Built with Java Swing for the GUI
- Uses Selenium WebDriver for browser automation
- Multi-threaded architecture for parallel processing
- Packaged with Maven for dependency management
- Can be converted to standalone EXE using Launch4j

## Requirements

- Java 8 or higher
- Google Chrome browser
- Internet connection (only required for first run or when ChromeDriver needs updating)

## Usage

1. Launch the application
2. Enter a list of URLs (one per line) or import from a file
3. Provide login credentials if needed
4. Click "Start Automation" to begin processing
5. Monitor progress in the status bar
6. Results will be saved to "danh_sach_url_da_dung.txt"

## Build Instructions

### JAR File
```
mvn clean package
```

### Executable (EXE)
1. Build the JAR file with Maven
2. Use Launch4j with the provided configuration file:
```
launch4jc launch4j.xml
``` 