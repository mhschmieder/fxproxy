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
import java.net.PasswordAuthentication;

/**
 * NOTE: This class is designed to be overridden so that specific application
 *  level GUI classes can manage the gathering of proper proxy login credentials.
 * NOTE: We could also handle server type (in another class?), if direct login.
 */
public abstract class ProxyAuthenticator extends Authenticator {

    // Declare a structure to hold the most recent login for any proxy.
    public PasswordAuthentication _passwordAuthentication;

    public ProxyAuthenticator() {
        super();
    }

    // NOTE: The proxy authenticator isn't invoked until the first time a
    //  network request is made.
    // TODO: Take note of whether this happens just once, or on every server
    //  request.
    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        // Do not attempt to authenticate if there is no proxy.
        final RequestorType requestorType = getRequestorType();
        final boolean hasProxy = RequestorType.PROXY.equals( requestorType );
        if ( hasProxy ) {
            // If necessary, grab the proxy credentials (one-time only).
            requestProxyCredentials();

            // Return the network proxy authenticator (set by overrider).
            return _passwordAuthentication;
        }

        // If no proxy, defer to the superclass default implementation.
        return super.getPasswordAuthentication();
    }

    public abstract void requestProxyCredentials();

}
