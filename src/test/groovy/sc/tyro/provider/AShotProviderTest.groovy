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

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import sc.tyro.core.component.Button

import java.nio.file.Path

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.not
import static org.hamcrest.io.FileMatchers.anExistingFile
import static sc.tyro.core.Config.setScreenshotProvider
import static sc.tyro.core.Tyro.*
import static WebdriverExtension.BASE_URL
import static WebdriverExtension.driver

@ExtendWith(WebdriverExtension)
@DisplayName("Screenshot Tests")
class AShotProviderTest {
    @BeforeAll
    static void before() {
        File screenshotDir = Path.of(System.getProperty("user.dir"), 'target', 'screenshots').toFile()
        screenshotDir.deleteDir()

        screenshotProvider = new AShotProvider(driver)

        visit BASE_URL + 'index.html'
    }

    @Test
    @DisplayName("Should take window screenshot")
    @Order(1)
    void windowScreenshot() {
        File screenshot = new File("target/screenshots/window.png")

        assertThat(screenshot, not(anExistingFile()))

        takeScreenshot('window')

        assertThat(screenshot, anExistingFile())
    }

    @Test
    @DisplayName("Should take component screenshot")
    @Order(2)
    void componentScreenshot() {
        Button button = $('button') as sc.tyro.bundle.html5.Button
        File screenshot = new File("target/screenshots/component.png")

        assertThat(screenshot, not(anExistingFile()))

        takeScreenshot('component', button)

        assertThat(screenshot, anExistingFile())
    }
}