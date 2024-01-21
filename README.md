# fxproxy
Wrapper for Java proxy handling.

This code is split off from commonstoolkit as not many people need to handle proxies, so it is better to have it in a specialized library even if it's a tiny code set overall.

Furthermore, the dependency on proxy-vole is highly volatile, which creates problems for users of my commonstoolkit as sometimes it disappears from Maven Central and other easily accessible repositories and/or changes vendors and/or implementations and strategies (and dependencies) yet again.

This library also now includes a JavaFX dialog wrapper and interrupt driven proxy handler, which previously was in a single-class library so that this library didn't depend on JavaFX or other GUI toolkits, but the functionality of this toolkit is useless without a Dialog for Proxy Login, so the two libraries are now merged, and clients can use this library to derive their own Swing-based Proxy Login Dialog if they don't want to use JavaFX.

