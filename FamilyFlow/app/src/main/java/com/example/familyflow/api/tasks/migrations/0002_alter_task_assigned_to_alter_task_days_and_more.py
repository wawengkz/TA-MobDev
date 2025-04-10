# Generated by Django 5.1.7 on 2025-03-21 08:29

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('tasks', '0001_initial'),
    ]

    operations = [
        migrations.AlterField(
            model_name='task',
            name='assigned_to',
            field=models.CharField(blank=True, max_length=255, null=True),
        ),
        migrations.AlterField(
            model_name='task',
            name='days',
            field=models.CharField(max_length=255),
        ),
        migrations.AlterField(
            model_name='task',
            name='room_type',
            field=models.CharField(max_length=100),
        ),
    ]
