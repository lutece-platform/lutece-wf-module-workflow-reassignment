/*
 * Copyright (c) 2002-2014, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.workflow.modules.reassignment.service;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import fr.paris.lutece.plugins.workflow.business.task.TaskTypeBuilder;
import fr.paris.lutece.plugins.workflowcore.business.task.ITaskType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

/**
 * Produces the reassignment task type.
 */
@ApplicationScoped
public class TaskTypeReassignmentProducer
{
    public static final String BEAN_TASK_TYPE = "workflow-reassignment.taskTypeReassignment";

    /**
     * Produces the reassignment task type from the plugin properties.
     *
     * @param key
     *            the task type key
     * @param titleI18nKey
     *            the i18n key of the title
     * @param beanName
     *            the name of the task bean
     * @param configBeanName
     *            the name of the config bean
     * @param configRequired
     *            true if the task needs a configuration
     * @param formTaskRequired
     *            true if the task displays a form
     * @param taskForAutomaticAction
     *            true if the task can be used by an automatic action
     * @return the task type
     */
    @Produces
    @ApplicationScoped
    @Named( BEAN_TASK_TYPE )
    public ITaskType produceTaskTypeReassignment( @ConfigProperty( name = "workflow-reassignment.taskTypeReassignment.key" ) String key,
            @ConfigProperty( name = "workflow-reassignment.taskTypeReassignment.titleI18nKey" ) String titleI18nKey,
            @ConfigProperty( name = "workflow-reassignment.taskTypeReassignment.beanName" ) String beanName,
            @ConfigProperty( name = "workflow-reassignment.taskTypeReassignment.configBeanName" ) String configBeanName,
            @ConfigProperty( name = "workflow-reassignment.taskTypeReassignment.configRequired", defaultValue = "false" ) boolean configRequired,
            @ConfigProperty( name = "workflow-reassignment.taskTypeReassignment.formTaskRequired", defaultValue = "false" ) boolean formTaskRequired,
            @ConfigProperty( name = "workflow-reassignment.taskTypeReassignment.taskForAutomaticAction", defaultValue = "false" ) boolean taskForAutomaticAction )
    {
        return TaskTypeBuilder.buildTaskType( key, titleI18nKey, beanName, configBeanName, configRequired, formTaskRequired, taskForAutomaticAction );
    }
}
