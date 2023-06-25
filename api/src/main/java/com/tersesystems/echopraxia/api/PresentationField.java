package com.tersesystems.echopraxia.api;

import com.tersesystems.echopraxia.spi.AttributesAware;
import com.tersesystems.echopraxia.spi.PresentationHints;

public interface PresentationField
    extends Field, PresentationHints<PresentationField>, AttributesAware<PresentationField> {}
