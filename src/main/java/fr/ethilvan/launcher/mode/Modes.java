package fr.ethilvan.launcher.mode;

import javax.swing.ComboBoxModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import fr.ethilvan.launcher.Provider;

public class Modes implements ComboBoxModel {

    private int current;
    private Mode[] list;

    private final transient EventListenerList listeners =
            new EventListenerList();

    public Modes() {
        this.current = 0;
        this.list = Provider.get().modes.clone();
    }

    @Override
    public int getSize() {
        return list.length;
    }

    @Override
    public Mode getElementAt(int index) {
        return list[index];
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

        for (int i = 0; i < list.length; i++) {
            if (list[i] == item) {
                current = i;
                return;
            }
        }

        throw new UnknownError();
    }

    @Override
    public Mode getSelectedItem() {
        return list[current];
    }

    public int getSelectedIndex() {
        return current;
    }

    public void addMode(Mode mode) throws AlreadyRegisteredMode {
        for (Mode existingMode : list) {
            if (mode.getName().equals(existingMode.getName())) {
                throw new AlreadyRegisteredMode(mode);
            }
        }

        int index = list.length;

        Mode[] oldList = list;
        list = new Mode[index + 1];
        System.arraycopy(oldList, 0, list, 0, index);
        list[index] = mode;

        for (ListDataListener listener :
                listeners.getListeners(ListDataListener.class)) {
            listener.intervalAdded(new ListDataEvent(this,
                    ListDataEvent.INTERVAL_ADDED, index, index));
        }
    }
}
