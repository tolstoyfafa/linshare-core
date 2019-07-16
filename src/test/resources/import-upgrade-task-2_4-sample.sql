-- TASK: UPGRADE_2_4_UPDATE_TARGET_DOMAIN_UUID_MAIL_ATTACHMENT_AUDIT
  INSERT INTO upgrade_task
  (id,
  uuid,
  identifier,
  task_group,
  parent_uuid,
  parent_identifier,
  task_order,
  status,
  priority,
  creation_date,
  modification_date,
  extras)
VALUES
 (31,
 'UNDEFINED',
 'UPGRADE_2_4_UPDATE_TARGET_DOMAIN_UUID_MAIL_ATTACHMENT_AUDIT',
 'UPGRADE_2_4',
  null,
  null,
  31,
 'NEW',
 'MANDATORY',
  now(),
  now(),
  null);