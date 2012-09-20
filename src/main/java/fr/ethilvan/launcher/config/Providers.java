package fr.ethilvan.launcher.config;

import javax.swing.ComboBoxModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataListener;

import fr.ethilvan.launcher.util.EthilVan;

public class Providers implements ComboBoxModel {

    private int current;
    private Provider[] providers;

    private final transient EventListenerList listeners =
            new EventListenerList();

    public Providers() {
        this.current = 0;
        this.providers = new Provider[] { EthilVan.getProvider() };
    }

    @Override
    public int getSize() {
        return providers.length;
    }

    @Override
    public Provider getElementAt(int index) {
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
        if (!(item instanceof Provider)) {
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
    public Provider getSelectedItem() {
        return providers[current];
    }
}
