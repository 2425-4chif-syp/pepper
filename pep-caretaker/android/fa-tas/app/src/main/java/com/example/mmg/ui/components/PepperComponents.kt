package com.example.mmg.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.mmg.ui.theme.PepperShapes

/**
 * Bausteine im Stil der Weboberflaeche.
 *
 * Zwei Dinge tragen das Gefuehl: Rueckmeldung kommt beim Druecken, nicht beim
 * Loslassen (.pressable im Web), und Bewegung laeuft kritisch gedaempft ab - kein
 * Nachfedern, weil hier keine Geste Schwung mitbringt.
 */

/** Wie --btn-focus-scale im Web: 0.97 beim Druecken. */
private const val PRESSED_SCALE = 0.97f

/**
 * Skalierung fuer Druck-Rueckmeldung. An eine [InteractionSource] gehaengt, die auch
 * das Klick-Modifier benutzt - damit reagiert die Flaeche auf pointer-down und nicht
 * erst auf den Klick.
 *
 * Die Feder ist kritisch gedaempft (dampingRatio 1.0): sie laeuft auf den Zielwert zu
 * und bleibt dort stehen. Ein Nachwippen waere hier falsch, die Geste hat keinen
 * Schwung, den man weitertragen koennte.
 */
@Composable
fun pressScale(interactionSource: InteractionSource, enabled: Boolean = true): Float {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) PRESSED_SCALE else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )
    return scale
}

/**
 * Karte der Uebersichtsliste - dasselbe Rezept wie .surface-card im Web:
 * Flaeche base-100, feine Linie in base-300, Radius --r-card, weicher Schatten.
 *
 * Kein Ripple: die Rueckmeldung ist die Skalierung. Ein Ripple ist Material-Sprache
 * und wuerde hier aus dem Rahmen fallen.
 */
@Composable
fun PepperCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale = pressScale(interactionSource)

    Surface(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = PepperShapes.card,
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shadowElevation = 2.dp,
        content = { content() }
    )
}

/**
 * Segmentierte Auswahl wie im iOS-Stil: eine ruhige Spur, darin eine angehobene
 * weisse Kachel auf der aktiven Seite. Ersetzt zwei Schaltflaechen, die beide wie
 * eine Aktion aussahen - hier ist auf den ersten Blick klar, dass genau eines von
 * beiden gilt.
 */
@Composable
fun PepperSegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = PepperShapes.pill,
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Row(
            modifier = Modifier.padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            options.forEachIndexed { index, label ->
                PepperSegment(
                    label = label,
                    selected = index == selectedIndex,
                    onClick = { onSelect(index) }
                )
            }
        }
    }
}

@Composable
private fun PepperSegment(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale = pressScale(interactionSource)

    // Die Kachel wandert nicht, sie wechselt die Seite - deshalb blenden Flaeche,
    // Schatten und Schriftfarbe gemeinsam um. 300ms, kritisch gedaempft.
    val spec = spring<Color>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMediumLow
    )
    val background by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.surface else Color.Transparent,
        animationSpec = spec
    )
    val labelColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = spec
    )
    val elevation by animateDpAsState(
        targetValue = if (selected) 2.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    )

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(elevation, PepperShapes.pill)
            .background(background, PepperShapes.pill)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .defaultMinSize(minWidth = 132.dp)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = labelColor
        )
    }
}
