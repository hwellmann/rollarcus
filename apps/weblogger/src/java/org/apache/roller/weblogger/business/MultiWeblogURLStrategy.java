/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */

package org.apache.roller.weblogger.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.roller.weblogger.config.WebloggerRuntimeConfig;
import org.apache.roller.weblogger.pojos.WeblogTheme;
import org.apache.roller.weblogger.pojos.Weblog;
import org.apache.roller.weblogger.util.URLUtilities;


/**
 * A Weblogger URLStrategy which builds urls for a multi-weblog environment.
 */
public class MultiWeblogURLStrategy extends AbstractURLStrategy {
    
    public MultiWeblogURLStrategy() {}
    
    
    /**
     * @inheritDoc
     */
    public URLStrategy getPreviewURLStrategy(String previewTheme) {
        return new PreviewURLStrategy(previewTheme);
    }
    
    
    /**
     * Get root url for a given weblog.  Optionally for a certain locale.
     */
    public String getWeblogURL(Weblog weblog,
                                            String locale,
                                            boolean absolute) {
        
        if(weblog == null) {
            return null;
        }
        
        StringBuffer url = new StringBuffer();
        
        if(absolute) {
            url.append(WebloggerRuntimeConfig.getAbsoluteContextURL());
        } else {
            url.append(WebloggerRuntimeConfig.getRelativeContextURL());
        }
        
        url.append("/").append(weblog.getHandle()).append("/");
        
        if(locale != null) {
            url.append(locale).append("/");
        }
        
        return url.toString();
    }
    
    
    /**
     * Get url for a single weblog entry on a given weblog.
     */
    public String getWeblogEntryURL(Weblog weblog,
                                                 String locale,
                                                 String entryAnchor,
                                                 boolean absolute) {
        
        if(weblog == null || entryAnchor == null) {
            return null;
        }
        
        StringBuffer url = new StringBuffer();
        
        url.append(getWeblogURL(weblog, locale, absolute));
        url.append("entry/").append(URLUtilities.encode(entryAnchor));
        
        return url.toString();
    }
    
    
    /**
     * Get url for a single weblog entry comments on a given weblog.
     */
    public String getWeblogCommentsURL(Weblog weblog,
                                                    String locale,
                                                    String entryAnchor,
                                                    boolean absolute) {
        
        return getWeblogEntryURL(weblog, locale, entryAnchor, absolute)+"#comments";
    }
    
    
    /**
     * Get url for a single weblog entry comment on a given weblog.
     */
    public String getWeblogCommentURL(Weblog weblog,
                                                   String locale,
                                                   String entryAnchor,
                                                   String timeStamp,
                                                   boolean absolute) {
        
        return getWeblogEntryURL(weblog, locale, entryAnchor, absolute)+"#comment-"+timeStamp;
    }
    
    
    /**
     * Get url for a collection of entries on a given weblog.
     */
    public String getWeblogCollectionURL(Weblog weblog,
                                                      String locale,
                                                      String category,
                                                      String dateString,
                                                      List tags,
                                                      int pageNum,
                                                      boolean absolute) {
        
        if(weblog == null) {
            return null;
        }
        
        StringBuffer pathinfo = new StringBuffer();
        Map params = new HashMap();
        
        pathinfo.append(getWeblogURL(weblog, locale, absolute));
        
        String cat = null;
        if(category != null && "/".equals(category)) {
            cat = null;
        } else if(category != null && category.startsWith("/")) {
            cat = category.substring(1);
        }
        
        if(cat != null && dateString == null) {
            pathinfo.append("category/").append(URLUtilities.encode(cat));
            
        } else if(dateString != null && cat == null) {
            pathinfo.append("date/").append(dateString);  
        
        } else if(tags != null && tags.size() > 0) {
            pathinfo.append("tags/").append(URLUtilities.getEncodedTagsString(tags));
        } else {
            if(dateString != null) params.put("date", dateString);
            if(cat != null) params.put("cat", URLUtilities.encode(cat));
        }

        if(pageNum > 0) {
            params.put("page", Integer.toString(pageNum));
        }
        
        return pathinfo.toString() + URLUtilities.getQueryString(params);
    }
    
    
    /**
     * Get url for a custom page on a given weblog.
     */
    public String getWeblogPageURL(Weblog weblog,
                                                String locale,
                                                String pageLink,
                                                String entryAnchor,
                                                String category,
                                                String dateString,
                                                List tags,
                                                int pageNum,
                                                boolean absolute) {
        
        if(weblog == null) {
            return null;
        }
        
        StringBuffer pathinfo = new StringBuffer();
        Map params = new HashMap();
        
        pathinfo.append(getWeblogURL(weblog, locale, absolute));
        
        if(pageLink != null) {
            pathinfo.append("page/").append(pageLink);
            
            // for custom pages we only allow query params
            if(dateString != null) {
                params.put("date", dateString);
            }
            if(category != null) {
                params.put("cat", URLUtilities.encode(category));
            }
            if(tags != null && tags.size() > 0) {
                params.put("tags", URLUtilities.getEncodedTagsString(tags));
            }
            if(pageNum > 0) {
                params.put("page", Integer.toString(pageNum));
            }
        } else {
            // if there is no page link then this is just a typical collection url
            return getWeblogCollectionURL(weblog, locale, category, dateString, tags, pageNum, absolute);
        }
        
        return pathinfo.toString() + URLUtilities.getQueryString(params);
    }
    
    
    /**
     * Get url for a feed on a given weblog.
     */
    public String getWeblogFeedURL(Weblog weblog,
                                                String locale,
                                                String type,
                                                String format,
                                                String category,
                                                String term,
                                                List tags,
                                                boolean excerpts,
                                                boolean absolute) {
        
        if(weblog == null) {
            return null;
        }
        
        StringBuffer url = new StringBuffer();
        
        url.append(getWeblogURL(weblog, locale, absolute));
        url.append("feed/").append(type).append("/").append(format);
        
        Map params = new HashMap();
        if(category != null && category.trim().length() > 0) {
            params.put("cat", URLUtilities.encode(category));
        }
        if(tags != null && tags.size() > 0) {
          params.put("tags", URLUtilities.getEncodedTagsString(tags));
        }
        if(term != null && term.trim().length() > 0) {
            params.put("q", URLUtilities.encode(term.trim()));
        }
        if(excerpts) {
            params.put("excerpts", "true");
        }
        
        return url.toString() + URLUtilities.getQueryString(params);
    }
    
    
    /**
     * Get url to search endpoint on a given weblog.
     */
    public String getWeblogSearchURL(Weblog weblog,
                                                  String locale,
                                                  String query,
                                                  String category,
                                                  int pageNum,
                                                  boolean absolute) {
        
        if(weblog == null) {
            return null;
        }
        
        StringBuffer url = new StringBuffer();
        
        url.append(getWeblogURL(weblog, locale, absolute));
        url.append("search");
        
        Map params = new HashMap();
        if(query != null) {
            params.put("q", URLUtilities.encode(query));
            
            // other stuff only makes sense if there is a query
            if(category != null) {
                params.put("cat", URLUtilities.encode(category));
            }
            if(pageNum > 0) {
                params.put("page", Integer.toString(pageNum));
            }
        }
        
        return url.toString() + URLUtilities.getQueryString(params);
    }
    
    
    /**
     * Get url to a resource on a given weblog.
     */
    public String getWeblogResourceURL(Weblog weblog,
                                                    String filePath,
                                                    boolean absolute) {
        
        if(weblog == null || StringUtils.isEmpty(filePath)) {
            return null;
        }
        
        StringBuffer url = new StringBuffer();
        
        url.append(getWeblogURL(weblog, null, absolute));
        url.append("resource/");
        
        if(filePath.startsWith("/")) {
            url.append(filePath.substring(1));
        } else {
            url.append(filePath);
        }
        
        return url.toString();
    }
    
    
    /**
     * Get url to rsd file on a given weblog.
     */
    public String getWeblogRsdURL(Weblog weblog,
                                               boolean absolute) {
        
        if(weblog == null) {
            return null;
        }
        
        return getWeblogURL(weblog, null, absolute)+"rsd";
    }
    
    
    /**
     * Get url to JSON tags service url, optionally for a given weblog.
     */
    public String getWeblogTagsJsonURL(Weblog weblog,
                                                    boolean absolute,
                                                    int pageNum) {
        
        StringBuffer url = new StringBuffer();
        
        if (absolute) {
            url.append(WebloggerRuntimeConfig.getAbsoluteContextURL());
        } else {
            url.append(WebloggerRuntimeConfig.getRelativeContextURL());
        }
        
        // json tags service base
        url.append("/roller-services/tagdata/weblog/");
        
        // is this for a specific weblog or site-wide?
        if (weblog != null) {
            url.append(weblog.getHandle()).append("/");
        }
        
        if (pageNum > 0) {
            url.append("?page=" + pageNum);
        }
        
        return url.toString();
    }
    
}