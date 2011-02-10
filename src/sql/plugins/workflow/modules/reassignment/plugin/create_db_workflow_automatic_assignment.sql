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