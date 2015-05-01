/* Copyright (C) 2014  Stephan Kreutzer
 *
 * This file is part of epub2html1.
 *
 * epub2html1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * epub2html1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with epub2html1. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/OPFManifestEntry.java
 * @author Stephan Kreutzer
 * @since 2014-07-01
 */



import java.io.File;



public class OPFManifestEntry
{
    public OPFManifestEntry(String id, File hRef, String mediaType)
    {
        this.id = id;
        this.hRef = hRef;
        this.mediaType = mediaType;
    }
    
    public String GetID()
    {
        return this.id;
    }
    
    public File GetHRef()
    {
        return this.hRef;
    }
    
    public String GetMediaType()
    {
        return this.mediaType;
    }
    
    protected String id;
    protected File hRef;
    protected String mediaType;
}

