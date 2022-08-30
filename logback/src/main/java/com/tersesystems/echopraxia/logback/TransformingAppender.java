package com.tersesystems.echopraxia.logback;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import java.util.Iterator;

public abstract class TransformingAppender<E> extends UnsynchronizedAppenderBase<E>
    implements AppenderAttachable<E> {

  protected AppenderAttachableImpl<E> aai = new AppenderAttachableImpl<E>();

  protected abstract E decorateEvent(E eventObject);

  @Override
  protected void append(E eventObject) {
    aai.appendLoopOnAppenders(decorateEvent(eventObject));
  }

  public void addAppender(Appender<E> newAppender) {
    addInfo("Attaching appender named [" + newAppender.getName() + "] to " + this.toString());
    aai.addAppender(newAppender);
  }

  public Iterator<Appender<E>> iteratorForAppenders() {
    return aai.iteratorForAppenders();
  }

  public Appender<E> getAppender(String name) {
    return aai.getAppender(name);
  }

  public boolean isAttached(Appender<E> eAppender) {
    return aai.isAttached(eAppender);
  }

  public void detachAndStopAllAppenders() {
    aai.detachAndStopAllAppenders();
  }

  public boolean detachAppender(Appender<E> eAppender) {
    return aai.detachAppender(eAppender);
  }

  public boolean detachAppender(String name) {
    return aai.detachAppender(name);
  }
}
