/** 
 *  Copyright (c) 2011-2014 Eclectic Logic LLC. 
 *  All rights reserved. 
 *   
 *  This software is the confidential and proprietary information of 
 *  Eclectic Logic LLC ("Confidential Information").  You shall 
 *  not disclose such Confidential Information and shall use it only
 *  in accordance with the terms of the license agreement you entered 
 *  into with Eclectic Logic LLC.
 *
 **/
package com.eclecticlogic.pedal.loader;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.eclecticlogic.pedal.loader.impl.LoaderImpl;

/**
 * @author kabram.
 *
 */
@Configurable
@EnableAutoConfiguration
@ComponentScan
public class JpaConfiguration {

    @PersistenceContext
    EntityManager entityManager;


    @Bean
    Loader loader() {
        return new LoaderImpl(entityManager);
    }
}
