/*
 * Copyright © 2021 Ovea (d.avenante@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sc.tyro.provider

import io.github.bonigarcia.wdm.WebDriverManager
import io.javalin.Javalin
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxOptions
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import sc.tyro.web.WebBundle

import static io.github.bonigarcia.wdm.WebDriverManager.chromedriver
import static io.github.bonigarcia.wdm.WebDriverManager.firefoxdriver
import static io.javalin.http.staticfiles.Location.CLASSPATH
import static java.lang.Boolean.valueOf
import static java.lang.System.getenv
import static java.net.InetAddress.getByName
import static org.openqa.selenium.remote.Browser.CHROME
import static org.openqa.selenium.remote.Browser.FIREFOX

/**
 * @author David Avenante
 * @since 1.0.0
 */
class WebdriverExtension implements BeforeAllCallback, AfterAllCallback {
    private static Logger LOGGER = LoggerFactory.getLogger(WebdriverExtension.class)

    private static Javalin app
    public static WebDriver driver
    public static String BASE_URL

    private static WebDriverManager wdm
    private static String browser
    private static boolean isCI = valueOf(getenv('CI'))

    @Override
    void beforeAll(ExtensionContext extensionContext) throws Exception {
        app = Javalin.create({
            config -> config.addStaticFiles("/webapp", CLASSPATH)
        }).start(0)

        DatagramSocket socket = new DatagramSocket()
        socket.connect(getByName("8.8.8.8"), 10002)
        String host_ip = socket.localAddress.hostAddress
        BASE_URL = "http://${host_ip}:${app.port()}/"

        browser = getenv('browser')
        if (!browser) {
            LOGGER.info('No browser selected. Use Chrome')
            browser = 'chrome'
        }

        if (FIREFOX.is(browser)) {
            wdm = firefoxdriver()
            FirefoxOptions options = new FirefoxOptions()
            options.addArguments('--width=1200')
            options.addArguments('--height=1500')
            wdm.capabilities(options)
        } else if (CHROME.is(browser)) {
            wdm = chromedriver()
            ChromeOptions options = new ChromeOptions()
            options.addArguments('--window-size=1200,1500')
            wdm.capabilities(options)
        }

        if (isCI) {
            wdm.browserInDocker().enableRecording()
        }

        driver = wdm.create()
        WebBundle.init(driver)
    }

    @Override
    void afterAll(ExtensionContext extensionContext) throws Exception {
        driver.close()
        wdm.quit()
        app.stop()
    }
}
