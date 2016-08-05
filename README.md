# Vert.x 3 Resteasy Example

This project contains example of how to use Vert.x 3 with resteasy to developer realtime web application easily.

To run the application, open it with IntelliJ and run App class

## Rest API

| resource      | method                       | description                       |
|:--------------|:-------------------------------|:----------------------------------|
| `/api/tasks`  |   POST   | save task
| `/api/tasks/{taskId}`  |   PUT   | update task
| `/api/tasks`  |   GET   | get all tasks
| `/api/tasks/{taskId}`  |   GET   | get specific task
| `/api/tasks/{taskId}`  |   DELETE   | delete task

### Response

```json
{
  "tasks": [
    {
      "id": "b6b2caef-c75b-4ab2-b851-a860eb8dd8c4",
      "completed": false,
      "message": "task3",
      "created": 1470392114079,
    },
    {
      "id": "15ce5449-dee9-4075-bf15-cc6af94297b4",
      "completed": false,
      "message": "task2",
      "created": 1470392114079,
    },
    {
      "id": "832a2034-535b-4e04-9a2f-b0987affe04a",
      "completed": false,
      "message": "task0",
      "created": 1470392114079,
    },
    {
      "id": "9d23587c-8bc4-46cb-962e-3090802bfd9b",
      "completed": false,
      "message": "task4",
      "created": 1470392114079,
    }
  ]
}


```

![image](https://cloud.githubusercontent.com/assets/66023/17433905/00cd6a74-5b10-11e6-92b0-d900ae5a8d54.png)
