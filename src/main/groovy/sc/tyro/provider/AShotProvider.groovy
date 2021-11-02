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

import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import ru.yandex.qatools.ashot.AShot
import ru.yandex.qatools.ashot.Screenshot
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider
import sc.tyro.core.component.Component
import sc.tyro.core.provider.ScreenshotProvider

import java.nio.file.Path

import static java.nio.file.Files.createDirectories
import static javax.imageio.ImageIO.write
import static org.openqa.selenium.By.id
import static ru.yandex.qatools.ashot.shooting.ShootingStrategies.scaling
import static ru.yandex.qatools.ashot.shooting.ShootingStrategies.viewportPasting

class AShotProvider implements ScreenshotProvider {
    private final WebDriver webDriver

    AShotProvider(WebDriver webDriver) {
        this.webDriver = webDriver
    }

    @Override
    void takeScreenshot(String name, Component component = null) {
        Screenshot screenshot

        if (component) {
            screenshot = new AShot()
                    .coordsProvider(new WebDriverCoordsProvider())
                    .takeScreenshot(webDriver, webDriver.findElement(id(component.id())))
        } else {
            JavascriptExecutor js = (JavascriptExecutor) webDriver
            Integer dpr = (Integer) js.executeScript('return window.devicePixelRatio')
            screenshot = new AShot()
                    .shootingStrategy(viewportPasting(scaling(dpr), 100))
                    .takeScreenshot webDriver
        }

        Path target = Path.of(System.getProperty("user.dir"), 'target', 'screenshots', name + '.png')
        createDirectories(target.getParent())

        write(screenshot.getImage(), "PNG", target.toFile())
    }
}
