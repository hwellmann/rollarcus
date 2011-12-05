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

package org.apache.roller.planet.business;

import java.util.Date;
import java.util.List;
import org.apache.roller.planet.pojos.Planet;
import org.apache.roller.planet.pojos.SubscriptionEntry;
import org.apache.roller.planet.pojos.PlanetGroup;
import org.apache.roller.planet.pojos.Subscription;
import org.apache.roller.weblogger.WebloggerException;


/**
 * Manages Planets, Groups, Subscriptions, and Entries.
 */
public interface PlanetManager extends Manager {
    
    
    public void savePlanet(Planet planet) throws WebloggerException;
    
    
    public void deletePlanet(Planet planet) throws WebloggerException;
    
    
    public Planet getPlanet(String handle) throws WebloggerException;
    
    
    public Planet getPlanetById(String id) throws WebloggerException;
    
    
    public List getPlanets() throws WebloggerException;
    
    
    /**
     * Save new or update existing a group
     */
    public void saveGroup(PlanetGroup sub) throws WebloggerException;
    
    
    /** 
     * Delete group and any subscriptions that are orphaned. 
     */
    public void deleteGroup(PlanetGroup group) throws WebloggerException;
    
    
    public PlanetGroup getGroup(Planet planet, String handle) throws WebloggerException;
    
    
    /**
     * Get group by ID rather than handle.
     */
    public PlanetGroup getGroupById(String id) throws WebloggerException;
    
    
    /**
     * Save or update a subscription
     */
    public void saveSubscription(Subscription sub) throws WebloggerException;
    
    
    /** 
     * Delete subscription, remove it from groups, cache, etc. 
     */
    public void deleteSubscription(Subscription group) throws WebloggerException;
    
    
    /**
     * Get subscription by feedUrl.
     */
    public Subscription getSubscription(String feedUrl) throws WebloggerException;
    
    
    /**
     * Get subscription by ID rather than feedUrl.
     */
    public Subscription getSubscriptionById(String id) throws WebloggerException;
    
    
    /**
     * Get all subscriptions.
     */
    public List getSubscriptions() throws WebloggerException;
    
    
    /**
     * Get total number of subscriptions.
     */
    public int getSubscriptionCount() throws WebloggerException;
    
    
    /**
     * Get top X subscriptions.
     */
    public List getTopSubscriptions(int offset, int len) throws WebloggerException;
    
    
    /**
     * Get top X subscriptions, restricted by group.
     */
    public List getTopSubscriptions(PlanetGroup group, int offset, int len) 
        throws WebloggerException;
    
    
    /**
     * Save new or update existing entry
     */
    public void saveEntry(SubscriptionEntry entry) throws WebloggerException;
    
    
    /** 
     * Delete entry. 
     */
    public void deleteEntry(SubscriptionEntry entry) throws WebloggerException;
    
    
    /**
     * Delete all entries for a subscription.
     *
     * @param subscription The subscription to delete entries from.
     * @throws WebloggerException If there is a problem doing the delete.
     */
    public void deleteEntries(Subscription sub) throws WebloggerException;
    
    
    /**
     * Lookup an entry by id.
     */
    public SubscriptionEntry getEntryById(String id) throws WebloggerException;
    
    
    /**
     * Get entries in a single feed as list of SubscriptionEntry objects.
     */
    public List getEntries(Subscription sub, int offset, int len) 
        throws WebloggerException;
    
    
    /**
     * Get Entries for a Group in reverse chonological order.
     *
     * @param group Restrict to entries from one group.
     * @param offset Offset into results (for paging)
     * @param len Maximum number of results to return (for paging)
     */
    public List getEntries(PlanetGroup group, int offset, int len) 
        throws WebloggerException;
    
    
    /**
     * Get Entries for a Group in reverse chonological order, optionally 
     * constrained to a certain timeframe.
     *
     * @param group Restrict to entries from one group.
     * @param startDate The oldest date for entries to include.
     * @param endDate The newest date for entries to include.
     * @param offset Offset into results (for paging)
     * @param len Maximum number of results to return (for paging)
     */
    public List getEntries(PlanetGroup group, 
                           Date startDate, 
                           Date endDate,
                           int offset, 
                           int len) throws WebloggerException;
    
}
