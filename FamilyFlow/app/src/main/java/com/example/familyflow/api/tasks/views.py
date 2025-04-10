from rest_framework import generics, status
from rest_framework.views import APIView
from rest_framework.response import Response
from .models import Task
from .serializers import TaskSerializer

class TaskListCreateView(generics.ListCreateAPIView):
    queryset = Task.objects.all()
    serializer_class = TaskSerializer

class TaskRetrieveUpdateDeleteView(generics.RetrieveUpdateDestroyAPIView):
    queryset = Task.objects.all()
    serializer_class = TaskSerializer

class TasksByRoomTypeView(generics.ListAPIView):
    serializer_class = TaskSerializer

    def get_queryset(self):
        """
        This view should return a list of all tasks for the specified room type.
        """
        room_type = self.kwargs['room_type']
        return Task.objects.filter(room_type=room_type)

class DeleteTasksByRoomTypeView(APIView):
    """
    Delete all tasks for a specific room type.
    """
    def delete(self, request, room_type):
        tasks = Task.objects.filter(room_type=room_type)
        count = tasks.count()
        tasks.delete()
        return Response({"message": f"Deleted {count} tasks from {room_type}"}, status=status.HTTP_204_NO_CONTENT)