package echopraxia.logback;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.slf4j.Marker;

/** A basic marker implementation that can be extended. */
public abstract class BaseMarker implements Marker {

  private final String name;
  private List<Marker> referenceList;

  public BaseMarker(String name) {
    requireNonNull(name, "A marker name cannot be null");
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public synchronized void add(Marker reference) {
    requireNonNull(reference, "A null value cannot be added to a Marker as reference.");

    if (!(this.contains(reference) || reference.contains(this))) {
      if (referenceList == null) {
        referenceList = new Vector<>();
      }
      referenceList.add(reference);
    }
  }

  public synchronized boolean hasReferences() {
    return referenceList != null && referenceList.size() > 0;
  }

  /**
   * @deprecated Replaced by {@link #hasReferences()}.
   */
  @Deprecated
  public boolean hasChildren() {
    return hasReferences();
  }

  public synchronized Iterator<Marker> iterator() {
    return hasReferences() ? referenceList.iterator() : Collections.emptyIterator();
  }

  public synchronized boolean remove(Marker referenceToRemove) {
    if (hasReferences()) {
      return referenceList.remove(referenceToRemove);
    } else {
      return false;
    }
  }

  public boolean contains(Marker other) {
    requireNonNull(other, "other cannot be null");

    if (this.equals(other)) {
      return true;
    } else if (hasReferences()) {
      return referenceList.stream().anyMatch(ref -> ref.contains(other));
    } else {
      return false;
    }
  }

  public boolean contains(String name) {
    requireNonNull(name, "name cannot be null");

    if (this.name.equals(name)) {
      return true;
    } else if (hasReferences()) {
      return referenceList.stream().anyMatch(ref -> ref.contains(name));
    } else {
      return false;
    }
  }

  private static final String OPEN = "[ ";
  private static final String CLOSE = " ]";
  private static final String SEP = ", ";

  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof Marker)) return false;

    final Marker other = (Marker) obj;
    return name.equals(other.getName());
  }

  public int hashCode() {
    return name.hashCode();
  }

  public String toString() {
    if (!this.hasReferences()) {
      return this.getName();
    }
    Iterator<Marker> it = this.iterator();
    Marker reference;
    StringBuilder sb = new StringBuilder(this.getName());
    sb.append(' ').append(OPEN);
    while (it.hasNext()) {
      reference = it.next();
      sb.append(reference.getName());
      if (it.hasNext()) {
        sb.append(SEP);
      }
    }
    sb.append(CLOSE);

    return sb.toString();
  }
}
