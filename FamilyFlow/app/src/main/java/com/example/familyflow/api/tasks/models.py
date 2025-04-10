from django.db import models

class Task(models.Model):
    name = models.CharField(max_length=255)
    # Store days as a comma-separated string (will be converted to/from a Set in the serializer)
    days = models.CharField(max_length=255)
    assigned_to = models.CharField(max_length=255, null=True, blank=True)
    is_done = models.BooleanField(default=False)
    room_type = models.CharField(max_length=100)

    def __str__(self):
        return f"{self.name} ({self.room_type})"