/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, JBoss Inc., and individual contributors as indicated
 * by the @authors tag.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package l.s.common.vfs.protocol;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * A File based URLStreamHandler
 *
 * @author <a href=mailto:jbailey@redhat.com">John Bailey</a>
 * @version $Revision$
 */
public class FileURLStreamHandler extends AbstractLocalURLStreamHandler {
    @Override
    protected URLConnection openConnection(final URL url) throws IOException {
        ensureLocal(url);
        return new FileURLConnection(url);
    }
}
