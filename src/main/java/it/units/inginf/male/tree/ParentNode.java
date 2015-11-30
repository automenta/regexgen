package it.units.inginf.male.tree;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by me on 11/29/15.
 */
abstract public class ParentNode extends AbstractNode {

    private final List<Node> children;
    private ParentNode parent;

//        public ParentNode() {
//            this(new ArrayList<>());
//        }

    public abstract int getMinChildrenCount();
    public abstract int getMaxChildrenCount();

    public ParentNode(List<Node> ch) {
        children = ch;
    }

    public final void add(Node... n) {
        Collections.addAll(children, n);
        unhash();
    }

    public final void add(Node n) {
        children.add(n);
        unhash();
    }

    public void set(int i, Node v) {
        children.set(i, v);
        unhash();
    }

    @Override
    public int hashCode() {
        final int hash = this.hash;
        if (hash == 0)
            return this.hash = rehash();
        return hash;
    }

    public void unhash() {
        hash = 0;
    }

    protected int rehash() {
        int h = Objects.hash(children);
        h = 31 * h + getClass().hashCode();
        if (h == 0) h = 1;
        return h;
    }

    @Override
    public final List<Node> children() {
        //return children;
        return Collections.unmodifiableList(children);
    }

    @Override
    public final ParentNode getParent() {
        return parent;
    }

    @Override
    public final void setParent(ParentNode parent) {
        this.parent = parent;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (getClass() != o.getClass()) return false;
        it.units.inginf.male.tree.ParentNode an = (it.units.inginf.male.tree.ParentNode) o;
        if (hashCode() != an.hashCode()) return false;
        return children().equals(an.children());
    }

    protected static void cloneChild(Node child, ParentNode parent) {
        Node newChild = child.cloneTree();
        newChild.setParent(parent);
        parent.add(newChild);
    }

}
