// Copyright 2013 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry5.internal.services.assets;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.apache.tapestry5.services.assets.AssetChecksumGenerator;
import org.apache.tapestry5.services.assets.StreamableResource;
import org.apache.tapestry5.services.assets.StreamableResourceProcessing;
import org.apache.tapestry5.services.assets.StreamableResourceSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class AssetChecksumGeneratorImpl implements AssetChecksumGenerator
{

    private final StreamableResourceSource streamableResourceSource;

    private final ResourceChangeTracker tracker;

    private final Map<StreamableResource, String> cache = CollectionFactory.newConcurrentMap();

    public AssetChecksumGeneratorImpl(StreamableResourceSource streamableResourceSource, ResourceChangeTracker tracker)
    {
        this.streamableResourceSource = streamableResourceSource;
        this.tracker = tracker;

        tracker.clearOnInvalidation(cache);
    }

    public String generateChecksum(Resource resource) throws IOException
    {
        StreamableResource streamable = streamableResourceSource.getStreamableResource(resource, StreamableResourceProcessing.COMPRESSION_DISABLED,
                tracker);

        return generateChecksum(streamable);
    }

    @Override
    public String generateChecksum(StreamableResource resource) throws IOException
    {
        String result = cache.get(resource);

        if (result == null)
        {
            result = toChecksum(resource.openStream());

            cache.put(resource, result);
        }

        return result;
    }

    private String toChecksum(InputStream is) throws IOException
    {
        byte[] digest = DigestUtils.md5(is);

        return Hex.encodeHexString(digest);
    }

}
