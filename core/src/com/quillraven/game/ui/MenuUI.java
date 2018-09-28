package com.quillraven.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.quillraven.game.core.ui.HUD;
import com.quillraven.game.core.ui.TTFSkin;

public class MenuUI extends Table {
    private final TTFSkin skin;

    private final Table mainPage;
    private final Table creditsPage;

    private final TextButton continueItem;
    private final Slider volumeSlider;
    private final Array<TextButton> menuItems;
    private int currentItemIdx;
    private int volumeIdx;
    private int creditsIdx;

    public MenuUI(final HUD hud, final TTFSkin skin) {
        super();
        this.skin = skin;

        menuItems = new Array<>();
        final Stack menuPages = new Stack();
        volumeSlider = new Slider(0, 100, 1, false, skin, "default");
        volumeSlider.setValue(80);
        continueItem = new TextButton("[Deactivated]Continue", skin, "big");
        mainPage = createMainPage(hud, skin);
        menuPages.add(mainPage);
        currentItemIdx = 0;
        volumeIdx = 1;
        creditsIdx = 2;
        highlightCurrentItem(true);

        creditsPage = createCreditsPage(hud, skin);
        menuPages.add(creditsPage);
        creditsPage.setVisible(false);

        add(menuPages).expand().fill();
    }

    private Table createMainPage(final HUD hud, final TTFSkin skin) {
        final Table content = new Table();

        menuItems.add(new TextButton(hud.getLocalizedString("newGame"), skin, "big"));
        content.add(menuItems.peek()).fill().expand().row();
        content.add(continueItem).fill().expand().row();

        final Table soundTable = new Table();
        menuItems.add(new TextButton(hud.getLocalizedString("volume"), skin, "big"));
        soundTable.add(menuItems.peek()).fillX().expandX().row();
        soundTable.add(volumeSlider).expandX().width(250);
        content.add(soundTable).fill().expand().row();

        menuItems.add(new TextButton(hud.getLocalizedString("creditsMenuItem"), skin, "big"));
        content.add(menuItems.peek()).fill().expand().row();
        menuItems.add(new TextButton(hud.getLocalizedString("quitGame"), skin, "big"));
        content.add(menuItems.peek()).fill().expand().row();

        content.padTop(200).padBottom(200);
        return content;
    }


    private Table createCreditsPage(final HUD hud, final TTFSkin skin) {
        final Table content = new Table();

        final TextButton creditsTxt = new TextButton(hud.getLocalizedString("credits"), skin, "normal");
        creditsTxt.getLabel().setWrap(true);
        creditsTxt.getLabel().setAlignment(Align.topLeft);
        content.add(new TextButton(hud.getLocalizedString("creditsMenuItem") + ":", skin, "big")).fillX().expandX().top().padTop(25).row();
        content.add(creditsTxt).fill().expand().top().pad(50, 25, 0, 25);

        content.top();
        return content;
    }

    private void highlightCurrentItem(final boolean highlight) {
        final Label label = menuItems.get(currentItemIdx).getLabel();
        if (highlight) {
            label.getText().insert(0, "[Highlight]");
        } else {
            label.getText().replace("[Highlight]", "");
        }
        label.invalidateHierarchy();
    }

    public void selectCurrentItem() {
        if (currentItemIdx == creditsIdx && !creditsPage.isVisible()) {
            mainPage.setVisible(false);
            creditsPage.setVisible(true);
        } else if (creditsPage.isVisible()) {
            creditsPage.setVisible(false);
            mainPage.setVisible(true);
        }
    }

    public void moveSelectionUp() {
        if (!mainPage.isVisible()) {
            return;
        }

        highlightCurrentItem(false);
        --currentItemIdx;
        if (currentItemIdx < 0) {
            currentItemIdx = menuItems.size - 1;
        }
        highlightCurrentItem(true);
    }

    public void moveSelectionDown() {
        if (!mainPage.isVisible()) {
            return;
        }

        highlightCurrentItem(false);
        ++currentItemIdx;
        if (currentItemIdx >= menuItems.size) {
            currentItemIdx = 0;
        }
        highlightCurrentItem(true);
    }

    public void moveSelectionLeft() {
        if (currentItemIdx == volumeIdx) {
            volumeSlider.setValue(volumeSlider.getValue() - volumeSlider.getStepSize());
            if (volumeSlider.getValue() == 0) {
                volumeSlider.setStyle(skin.get("deactivated", Slider.SliderStyle.class));
            }
        }
    }

    public void moveSelectionRight() {
        if (currentItemIdx == volumeIdx) {
            volumeSlider.setValue(volumeSlider.getValue() + volumeSlider.getStepSize());
            if (volumeSlider.getValue() == 1) {
                volumeSlider.setStyle(skin.get("default", Slider.SliderStyle.class));
            }
        }
    }

    public void activateContinueItem(final boolean activate) {
        highlightCurrentItem(false);
        final Label label = continueItem.getLabel();
        if (activate) {
            if (!menuItems.contains(continueItem, true)) {
                menuItems.insert(1, continueItem);
                label.getText().replace("[Deactivated]", "");
            }
            ++volumeIdx;
            ++creditsIdx;
            ++currentItemIdx;
        } else {
            menuItems.removeValue(continueItem, true);
            label.getText().insert(0, "[Deactivated]");
            --volumeIdx;
            --creditsIdx;
            --currentItemIdx;
        }
        label.invalidateHierarchy();
        highlightCurrentItem(true);
    }

    public boolean isNewGameSelected() {
        return currentItemIdx == 0;
    }

    public boolean isQuitGameSelected() {
        return currentItemIdx == menuItems.size - 1;
    }
}