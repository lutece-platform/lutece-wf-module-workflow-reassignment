-- liquibase formatted sql
-- changeset workflow-reassignment:create_db_workflow_automatic_assignment.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM workflow_task_reassignment_cf WHERE 1 = 0
DROP TABLE IF EXISTS workflow_task_reassignment_cf;


/*==============================================================*/
/* Table structure for table workflow_reassignment_cf			*/
/*==============================================================*/

CREATE TABLE  workflow_task_reassignment_cf (
  id_task INT  NOT NULL ,
  title VARCHAR(255) DEFAULT NULL, 
  is_notify SMALLINT DEFAULT 0,
  is_use_user_name SMALLINT DEFAULT 0,
  message LONG VARCHAR DEFAULT NULL,
  subject VARCHAR(45) DEFAULT NULL,
  PRIMARY KEY  (id_task)
) ;