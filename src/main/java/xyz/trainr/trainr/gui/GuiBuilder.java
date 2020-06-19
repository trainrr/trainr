package xyz.trainr.trainr.gui;

import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GuiBuilder {

    private int size = 6 * 9;
    private String title = "My Gui";
    private Set<GuiButton> buttons = new HashSet<>();
    private ItemStack[] contents = new ItemStack[6 * 9];

    public GuiBuilder() {
    }

    public GuiBuilder setSize(int i) {
        this.size = i;
        this.contents = Arrays.copyOf(contents, i);
        return this;
    }

    public GuiBuilder setItem(int slot, ItemStack item) {
        this.contents[slot] = item;
        return this;
    }

    public GuiBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public GuiBuilder addButton(GuiButton button) {
        buttons.add(button);
        return this;
    }

    public Gui build() {
        return new Gui(size, title, buttons, contents);
    }

}