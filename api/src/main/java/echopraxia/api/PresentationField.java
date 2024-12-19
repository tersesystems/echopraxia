package echopraxia.api;

import echopraxia.spi.AttributesAware;
import echopraxia.spi.PresentationHints;

public interface PresentationField
    extends Field, PresentationHints<PresentationField>, AttributesAware<PresentationField> {}
