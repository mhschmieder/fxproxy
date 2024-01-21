/**
 * MIT License
 *
 * Copyright (c) 2020, 2024 Mark Schmieder
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * This file is part of the FxProxy Library
 *
 * You should have received a copy of the MIT License along with the FxProxy
 * Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxproxy
 */
package com.mhschmieder.fxproxy.net;

import java.net.Authenticator;
import java.net.ProxySelector;

import com.github.markusbernhardt.proxy.ProxySearch;
import com.github.markusbernhardt.proxy.ProxySearch.Strategy;
import com.github.markusbernhardt.proxy.selector.direct.NoProxySelector;
import com.github.markusbernhardt.proxy.selector.misc.ProtocolDispatchSelector;

public final class ProxyUtilities {

    // Detect whether there is a proxy in effect (must set it first).
    // NOTE: Generally only "http" will be of interest, and also only if we
    //  know that we attempted to set a proxy and received a proxy selector.
    // NOTE: The timing of when the JavaFX-based Proxy Login is indirectly
    //  launched from the OS via the Proxy Authenticator is indeterminate,
    //  so it is safer to wrap this in a JavaFX thread so there's more chance
    //  the Proxy Login will have been engaged and dismissed and the proxy
    //  forwarding setup has completed by the time this code is invoked.
    // NOTE: This method hasn't been used in years, but might be necessary?
    public static boolean hasProxy( final String protocol ) throws SecurityException {
        final ProxySelector proxySelector = ProxySelector.getDefault();
        if ( ( proxySelector == null ) || ( proxySelector instanceof NoProxySelector ) ) {
            return false;
        }

        // When there is a valid proxy, Proxy-Vole will generally set it to
        // com.btr.proxy.selector.whitelist.ProxyBypassListSelector, but on
        // macOS, there is an extra level of indirection per protocol via the
        // custom ProtocolDispatchSelector container/dispatcher class.
        if ( proxySelector instanceof ProtocolDispatchSelector ) {
            final ProtocolDispatchSelector protocolDispatchSelector =
                                                                    ( ProtocolDispatchSelector ) proxySelector;
            final ProxySelector protocolProxySelector = protocolDispatchSelector
                    .getSelector( protocol );
            if ( ( protocolProxySelector == null )
                    || ( protocolProxySelector instanceof NoProxySelector ) ) {
                return false;
            }
        }

        return true;
    }

    // Install the appropriate proxy selector if a proxy is detected.
    public static ProxySelector installProxySelector() throws SecurityException {
        // Create a proxy search object with default settings. The default
        // settings chosen depend on the platform that we are currently running
        // on. Normally it is something like this: Try Java Proxy System
        // Properties, if not available try browser settings, if not available
        // try global desktop settings and finally try to detect proxy settings
        // in an environment variable.
        // NOTE: We have changed this to eliminate the Java and Browser Proxy
        //  Settings as they are not of interest in our context; only the
        //  OS-level Proxy Settings matter.
        final ProxySearch proxySearch = new ProxySearch();
        proxySearch.addStrategy( Strategy.OS_DEFAULT );

        // Invoke the proxy search. This will try to detect the proxy settings
        // as explained above and will create a ProxySelector for us that uses
        // the detected proxy settings.
        final ProxySelector proxySelector = proxySearch.getProxySelector();

        // Install this proxy selector as the default for all connections.
        ProxySelector.setDefault( proxySelector );

        // Let the invoker cache whether there is a default proxy or not.
        return proxySelector;
    }

    // Set the proxy if one is detected.
    public static ProxySelector setProxy( final ProxyAuthenticator proxyAuthenticator )
            throws SecurityException {
        // Install the appropriate proxy selector if a proxy is detected.
        final ProxySelector proxySelector = installProxySelector();

        // Set the authentication credentials for the proxy.
        setProxyAuthenticator( proxyAuthenticator );

        // Let the invoker cache whether there is a default proxy or not.
        return proxySelector;
    }

    // Set the authentication credentials for the proxy.
    // NOTE: The proxy authenticator isn't invoked until the first time a
    //  network request is made.
    public static void setProxyAuthenticator( final ProxyAuthenticator proxyAuthenticator ) {
        // Some proxy servers request a login from the user before they will
        // allow any connections. Proxy-Vole has no support to handle this
        // automatically. This needs to be done manually because there is no way
        // to read the username and password since these settings are stored in
        // an encrypted format. So we need to install an authenticator in our
        // program manually and ask the user via a Login Dialog to enter a
        // username and password.
        Authenticator.setDefault( proxyAuthenticator );
    }

}
