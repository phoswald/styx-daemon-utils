# styx-daemon-utils
Utilities for implementing container-friendly background processes

- styx.daemon.utils.Daemon: Utility for implementing long running applications that terminate *cleanly* upon SIGTERM.
- styx.daemon.utils.Arguments: Utility for reading arguments from the command line, system properties, or environent variables.
- styx.daemon.utils.LogConfig: Utility to initialize java.util.logging from a properties file on the classpath.
