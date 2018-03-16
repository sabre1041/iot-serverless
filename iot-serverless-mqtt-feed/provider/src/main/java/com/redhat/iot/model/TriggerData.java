/*
 * Copyright 2016-2017 Red Hat, Inc, and individual contributors.
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

package com.redhat.iot.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name="triggers")
public class TriggerData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="triggerName")
    private String triggerName;

    @Column(name="triggerNamespace")
    private String triggerNamespace;

    @Column(name="shortTriggerName")
    private String shortTriggerName;
    
    @Column(name="auth")
    private String authKey;
    
    @Column(name="topic")
    private String topic;
    
    public TriggerData() {
    }

    public TriggerData(String triggerName, String authKey) {
        this.triggerName = triggerName;
        this.authKey = authKey;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

	public String getAuthKey() {
		return authKey;
	}

	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getShortTriggerName() {
		return shortTriggerName;
	}

	public void setShortTriggerName(String shortTriggerName) {
		this.shortTriggerName = shortTriggerName;
	}

	public String getTriggerNamespace() {
		return triggerNamespace;
	}

	public void setTriggerNamespace(String triggerNamespace) {
		this.triggerNamespace = triggerNamespace;
	}
}
