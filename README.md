# Job Management Service

##TODO

###### 
-Swagger documentation

-Security URL

-Remove Embedded H2

-Dockerize for scalability


##Add Non Cron Job
http://localhost:8080/job-management/add-job

`{
"jobName":"FileIndexingJob",
"className":"com.payoneer.job.management.jobs.FileIndexingJob",
"priority":"LOW"
}`

##Add Cron Job
http://localhost:8080/job-management/add-job

`{
"jobName":"SendEmailJob",
"className":"com.payoneer.job.management.jobs.SendEmailJob",
"priority":"HIGH",
"cronExpression":"1 * * * * *"
}`

##Add Non Cron Job
http://localhost:8080/job-management/add-job

`{
"jobName":"LoadDWHJob",
"className":"com.payoneer.job.management.jobs.LoadDWHJob",
"priority":"HIGH"
}`

##Add Non Cron Low Priority Job
http://localhost:8080/job-management/add-job

`{
"jobName":"FileIndexingJob",
"className":"com.payoneer.job.management.jobs.FileIndexingJob",
"priority":"LOW"
}`

##Add Start Job Execution
http://localhost:8080/job-management/start-job-execution

`{}`

##Add Stop Job Execution
http://localhost:8080/job-management/stop-job-execution

`{}`
