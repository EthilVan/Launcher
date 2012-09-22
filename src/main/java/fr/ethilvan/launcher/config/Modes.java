package fr.ethilvan.launcher.config;

import javax.swing.ComboBoxModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataListener;

import fr.ethilvan.launcher.Provider;

public class Modes implements ComboBoxModel {

    private int current;
    private Mode[] providers;

    private final transient EventListenerList listeners =
            new EventListenerList();

    public Modes() {
        this.current = 0;
        this.providers = Provider.get().modes.clone();
    }

    @Override
    public int getSize() {
        return providers.length;
    }

    @Override
    public Mode getElementAt(int index) {
        return providers[index];
    }

    @Override
    public void addListDataListener(ListDataListener listener) {
        listeners.add(ListDataListener.class, listener);
    }

    @Override
    public void removeListDataListener(ListDataListener listener) {
        listeners.remove(ListDataListener.class, listener);
    }

    @Override
    public void setSelectedItem(Object item) {
        if (!(item instanceof Mode)) {
            throw new IllegalArgumentException(
                    "Item is not a Provider (" + item.getClass() + ")");
        }

        for (int i = 0; i < providers.length; i++) {
            if (providers[i] == item) {
                current = i;
                return;
            }
        }

        throw new UnknownError();
    }

    @Override
    public Mode getSelectedItem() {
        return providers[current];
    }

    public int getSelectedIndex() {
        return current;
    }
}
