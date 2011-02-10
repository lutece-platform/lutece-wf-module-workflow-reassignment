/*
 * Copyright (c) 2002-2009, Mairie de Paris
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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;


/**
 *
 *class   TaskAssignmentConfigDAO
 *
 */
public class TaskReassignmentConfigDAO implements ITaskReassignmentConfigDAO
{
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_task, title,is_notify,message,subject,is_use_user_name " +
        " FROM workflow_task_reassignment_cf WHERE id_task=?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO  workflow_task_reassignment_cf  " +
        "(id_task, title, is_notify,message,subject,is_use_user_name)VALUES(?,?,?,?,?,?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_task_reassignment_cf " +
        "SET id_task=?, title=?, is_notify=?, message = ?, subject = ?, is_use_user_name = ? " + " WHERE id_task=? ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_task_reassignment_cf  WHERE id_task=? ";

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.taskassignment.business.ITaskAssignmentConfigDAO#insert(fr.paris.lutece.plugins.workflow.modules.taskassignment.business.TaskAssignmentConfig, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public synchronized void insert( TaskReassignmentConfig config, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        int nPos = 0;

        daoUtil.setInt( ++nPos, config.getIdTask(  ) );
        daoUtil.setString( ++nPos, config.getTitle(  ) );
        daoUtil.setBoolean( ++nPos, config.isNotify(  ) );
        daoUtil.setString( ++nPos, config.getMessage(  ) );
        daoUtil.setString( ++nPos, config.getSubject(  ) );
        daoUtil.setBoolean( ++nPos, config.isUseUserName(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /* (non-Javadoc)
         * @see fr.paris.lutece.plugins.workflow.modules.taskassignment.business.ITaskAssignmentConfigDAO#store(fr.paris.lutece.plugins.workflow.modules.taskassignment.business.TaskAssignmentConfig, fr.paris.lutece.portal.service.plugin.Plugin)
         */
    public void store( TaskReassignmentConfig config, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        int nPos = 0;

        daoUtil.setInt( ++nPos, config.getIdTask(  ) );
        daoUtil.setString( ++nPos, config.getTitle(  ) );
        daoUtil.setBoolean( ++nPos, config.isNotify(  ) );
        daoUtil.setString( ++nPos, config.getMessage(  ) );
        daoUtil.setString( ++nPos, config.getSubject(  ) );
        daoUtil.setBoolean( ++nPos, config.isUseUserName(  ) );

        daoUtil.setInt( ++nPos, config.getIdTask(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.taskassignment.business.ITaskAssignmentConfigDAO#load(int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public TaskReassignmentConfig load( int nIdTask, Plugin plugin )
    {
        TaskReassignmentConfig config = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );

        daoUtil.setInt( 1, nIdTask );

        daoUtil.executeQuery(  );

        int nPos = 0;

        if ( daoUtil.next(  ) )
        {
            config = new TaskReassignmentConfig(  );
            config.setIdTask( daoUtil.getInt( ++nPos ) );
            config.setTitle( daoUtil.getString( ++nPos ) );
            config.setNotify( daoUtil.getBoolean( ++nPos ) );
            config.setMessage( daoUtil.getString( ++nPos ) );
            config.setSubject( daoUtil.getString( ++nPos ) );
            config.setUseUserName( daoUtil.getBoolean( ++nPos ) );
        }

        daoUtil.free(  );

        return config;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.taskassignment.business.ITaskAssignmentConfigDAO#delete(int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void delete( int nIdTask, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );

        daoUtil.setInt( 1, nIdTask );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }
}
