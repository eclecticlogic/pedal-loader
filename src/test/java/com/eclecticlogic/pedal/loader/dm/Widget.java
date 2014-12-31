/**
 * Copyright (c) 2014-2015 Eclectic Logic LLC
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.eclecticlogic.pedal.loader.dm;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author kabram.
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "widget")
public class Widget implements Serializable {

    private int id;
    private String name;
    private ExoticTypes type;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "login_type", nullable = false)
    public ExoticTypes getType() {
        return type;
    }


    public void setType(ExoticTypes type) {
        this.type = type;
    }

}
