/*
 * Copyright (c) 2002-2011, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.modules.taskreassignment.business;

import fr.paris.lutece.plugins.workflow.modules.taskassignment.business.WorkgroupConfig;
import fr.paris.lutece.plugins.workflow.modules.taskassignment.business.WorkgroupConfigHome;
import fr.paris.lutece.plugins.workflow.modules.taskreassignment.service.ReassignmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;


/**
 * This class provides instances management methods (create, find, ...) for TaskReassignmentConfig objects
 */
public final class TaskReassignmentConfigHome
{
    // Static variable pointed at the DAO instance
    private static ITaskReassignmentConfigDAO _dao = 
    	(ITaskReassignmentConfigDAO) SpringContextService.getPluginBean( ReassignmentPlugin.PLUGIN_NAME, "taskReassignmentConfigDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private TaskReassignmentConfigHome(  )
    {
    }

    /**
     * Creation of an instance of config
     *
     * @param config The instance of task which contains the informations to store
     * @param plugin the plugin
     *
     *
     */
    public static void create( TaskReassignmentConfig config, Plugin plugin, Plugin workflowPlugin )
    {
        _dao.insert( config, plugin );

        List<WorkgroupConfig> listWorkgroups = config.getWorkgroups(  );

        if ( listWorkgroups != null )
        {
            for ( WorkgroupConfig workgroupConfig : listWorkgroups )
            {
                WorkgroupConfigHome.create( workgroupConfig, workflowPlugin );
            }
        }
    }

    /**
     * Update of task which is specified in parameter
     *
     * @param  config The instance of config which contains the informations to update
     * @param plugin the Plugin
     *
     */
    public static void update( TaskReassignmentConfig config, Plugin plugin, Plugin workflowPlugin )
    {
        _dao.store( config, plugin );
        //update workgroups
        WorkgroupConfigHome.removeByTask( config.getIdTask(  ), workflowPlugin );

        List<WorkgroupConfig> listWorkgroups = config.getWorkgroups(  );

        if ( listWorkgroups != null )
        {
            for ( WorkgroupConfig workgroupConfig : listWorkgroups )
            {
                WorkgroupConfigHome.create( workgroupConfig, workflowPlugin );
            }
        }
    }

    /**
     *  remove config associated to the task which is specified in parameter
     *
     * @param nIdTask The task key
     * @param plugin the Plugin
     *
     */
    public static void remove( int nIdTask, Plugin plugin, Plugin workflowPlugin )
    {
        WorkgroupConfigHome.removeByTask( nIdTask, workflowPlugin );
        _dao.delete( nIdTask, plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
         * Load the Config Object
         * @param nIdTask the task id
         * @param plugin the plugin
         * @return the Config Object
         */
    public static TaskReassignmentConfig findByPrimaryKey( int nIdTask, Plugin plugin, Plugin workflowPlugin )
    {
        TaskReassignmentConfig config = _dao.load( nIdTask, plugin );

        if ( config != null )
        {
            config.setWorkgroups( WorkgroupConfigHome.getListByConfig( nIdTask, workflowPlugin ) );
        }

        return config;
    }
}
