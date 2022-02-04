@file:JvmName("Utils")
@file:JvmMultifileClass

package afm.common.utils

import afm.Main
import afm.anime.Anime
import javafx.beans.property.StringProperty
import javafx.beans.value.ChangeListener
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.control.TableColumn
import java.net.URL

fun getFxmlUrl(fname: String): URL? = classLoader.getResource("view/$fname.fxml")


// for java code
fun toIntOrNull(s: String): Int? = s.toIntOrNull()


// Stop user from typing any characters that aren't numeric
fun intOnlyListener(): ChangeListener<String?> =
    ChangeListener { obs, oldVal, newVal ->
        /* if (newVal.isNullOrEmpty())
            (obs as StringProperty).value = "0"
        else */
        if (!newVal.isStrictInteger())
            (obs as StringProperty).value = oldVal
    }
fun positiveIntOrEmptyListener(): ChangeListener<String?> =
    ChangeListener { obs, oldVal, newVal ->
        // only change back to oldVal if newVal isn't null/empty
        if (!newVal.isNullOrEmpty() && !newVal.isStrictInteger())
            (obs as StringProperty).value = oldVal
    }


fun sleep(millis: Long) = Thread.sleep(millis)


fun showAndWaitConfAlert(header: String?, content: String?): ButtonType {
    return Alert(AlertType.CONFIRMATION, content, ButtonType.YES, ButtonType.NO).run {
        initOwner(Main.getStage())
        headerText = header
        showAndWait().orElse(ButtonType.NO)
    }
}


private fun generateColumn(name: String, prefWidth: Double): TableColumn<Anime, Button> {
    return TableColumn<Anime, Button>(name).apply {
        isEditable = false
        isSortable = false
        style = "-fx-alignment: CENTER"
        this.prefWidth = prefWidth
    }
}

fun <T> getActionsCol(): TableColumn<Anime, T> {
    return TableColumn<Anime, T>("Actions").apply {
        isEditable = false
        isSortable = false
    }
}

/* For ResultsScreen */

fun getResultInfoCol(): TableColumn<Anime, Button> =
    generateColumn("See Info", 101.60003662109375)

fun getResultCol(name: String): TableColumn<Anime, Button> =
    generateColumn(name, 76.5)


/* For MyListScreen & ToWatchScreen */

fun getInfoCol(): TableColumn<Anime, Button> =
    generateColumn("See Info", 75.20001220703125)

fun getMoveCol(move: String): TableColumn<Anime, Button> =
    generateColumn("Move to $move", 109.5999755859375)

fun getRemoveCol(): TableColumn<Anime, Button> =
    generateColumn("Remove", 71.2000732421875)
