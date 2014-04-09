import os

from rest_framework import serializers

from models import GTFSFeed


class GTFSFeedSerializer(serializers.ModelSerializer):
    class Meta:
        model = GTFSFeed
        read_only_fields = ('is_valid', 'validation_results_file', 'validation_summary')

    def validate_source_file(self, attrs, source):
        """ Basic validation to ensure the file name ends in .zip. """
        fileobj = attrs[source]

        name, extension = os.path.splitext(fileobj.name)
        if extension != '.zip':
            raise serializers.ValidationError("Uploaded filename must end in .zip.")

        return attrs
