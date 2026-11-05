package adventure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An EntityList is a set-like list for Entities with the following properties:
 * <nl>
 *   <li>An Entity may be in at most one EntityList.</li>
 *   <li>Adding an Entity to an EntityList removes it from any other EntityList.</li>
 *   <li>List membership is determined by reference equality.</li>
 * </nl>
 * Some List API operations are not supported.
 * 
 * Each ItemList has a name for diagnostic purposes.
 */
public class EntityList<T extends Entity> extends ArrayList<T> implements List<T> {
    
    private static final long serialVersionUID = 1691058264794808856L;
    private final String name;
    
    public EntityList(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    @Override
    public boolean add(T elem) {
        if (elem.isDestroyed()) {
            throw new StateInconsistencyException("Entity is destroyed");
        }
        EntityList<? super T> other = (EntityList<? super T>) elem.getMemberOf();
        if (other != null) {
            other.remove(elem);
        }
        return super.add(elem);
    }

    @Override
    public void add(int pos, T elem) {
        if (elem.isDestroyed()) {
            throw new StateInconsistencyException("Entity is destroyed");
        }
        EntityList<?> other = elem.getMemberOf();
        if (other != null) {
            other.remove(elem);
        }
        super.add(pos, elem);
    }

    @Override
    public boolean addAll(Collection<? extends T> items) {
        for (T item: items) {
            add(item);
        }
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> items) {
        for (T item: items) {
            add(index++, item);
        }
        return true;
    }

        @Override
    public boolean remove(Object obj) {
        Entity elem = (Entity) obj;
        EntityList<?> other = elem.getMemberOf();
        if (other == null) {
            return false;
        } else if (other == this) {
            elem.setMemberOf(null);
            if (!super.remove(elem)) {
                throw new StateInconsistencyException("Entity not found in internal list");
            }
            return false;
        } else {
            throw new StateInconsistencyException("Entity is in a different EntityList");
        }
    }

    @Override
    public boolean removeAll(Collection<?> items) {
        for (Object obj: items) {
            remove(obj);
        }
        return true;

    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("ItemList::retainAll");
    }

    @Override
    public T set(int arg0, T arg1) {
        throw new UnsupportedOperationException("ItemList::set");
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("ItemList::sublist");
    }
}
