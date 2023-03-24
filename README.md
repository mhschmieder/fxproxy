# proxytoolkit
Wrapper for Java proxy handling.

This code is split off from commonstoolkit as not many people need to handle proxies, so it is better to have it in a specialized library even if it's a tiny code set overall.

Furthermore, the dependency on proxy-vole is highly volatile, which creates problems for users of my commonstoolkit as sometimes it disappears from Maven Central and other easily accessible repositories and/or changes vendors and/or implementations and strategies (and dependencies) yet again.
