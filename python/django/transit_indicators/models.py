from django.db import models

from datasources.models import Boundary, DemographicDataFieldName, DemographicDataSource


class OTIIndicatorsConfig(models.Model):
    """ Global configuration for indicator calculation. """
    # Suffixes denote database units; the UI may convert to other units for
    # display to users.
    # The local poverty line (in local currency, therefore no database units).
    poverty_line = models.FloatField()

    # The average fare (local currency, no database units)
    avg_fare = models.FloatField()

    # Buffer distance (meters) for indicators attempting to capture a concept of
    # nearness. E.g. "Percentage of elderly people living within X meters of a
    # bus stop."
    nearby_buffer_distance_m = models.FloatField()

    # The maximum allowable commute time (seconds). Used by the job accessibility
    # indicator to generate a travelshed for access to designated job
    # locations.
    max_commute_time_s = models.PositiveIntegerField()

    # Maximum allowable walk time (seconds). Also used by the job accessibility indicator
    # when generating its travelshed.
    max_walk_time_s = models.PositiveIntegerField()

    # Setting related_name to '+' prevents Django from creating the reverse
    # relationship, which prevents a conflict because otherwise each Boundary
    # would have two fields named 'otiindicatorsconfig_set'. If we wanted, we
    # could name them something else to preserve both reverse relationships.
    # Set key to null when referenced boundary is deleted.
    # Boundary denoting the city -- used for calculating percentage of system
    # falling inside city limits
    city_boundary = models.ForeignKey(Boundary, blank=True, null=True,
                                      on_delete=models.SET_NULL, related_name='+')

    # Boundary denoting the region
    # Set key to null when referenced boundary is deleted.
    region_boundary = models.ForeignKey(Boundary, blank=True, null=True,
                                        on_delete=models.SET_NULL, related_name='+')


class OTIDemographicConfig(models.Model):
    """Stores configuration relating to demographic data.

    When POSTing a JSON representation of this object to
    /demographics/<datasource-id>/load/ to load a new set of data and generate a new
    configuration, send field _names_ rather than the id of a DemographicDataFieldName
    object; the serializer will take care of looking up the proper objects.
    E.g. {"pop_metric_1_label": "DIST_POP" }.
    """
    # Labels and field names for demographic metrics, of which there will
    # always be precisely three (3): Two (2) population metrics and one (1)
    # destination metric.
    # The label is human-readable and designed to be displayed to users.
    # The field is for accessing the data from the associated shapefile.
    pop_metric_1_label = models.CharField(max_length=255, blank=True, null=True)
    pop_metric_1_field = models.ForeignKey(DemographicDataFieldName, blank=True,
                                           null=True, related_name='+')

    pop_metric_2_label = models.CharField(max_length=255, blank=True, null=True)
    pop_metric_2_field = models.ForeignKey(DemographicDataFieldName, blank=True,
                                           null=True, related_name='+')

    dest_metric_1_label = models.CharField(max_length=255, blank=True, null=True)
    dest_metric_1_field = models.ForeignKey(DemographicDataFieldName, blank=True,
                                            null=True, related_name='+')

    # The datasource from which these data points will come
    datasource = models.ForeignKey(DemographicDataSource)


class SamplePeriod(models.Model):
    """Stores configuration for a slice of time that is used to calculate indicators.

    A sample period is a period of time within a specific date (or potentially two
    subsequent dates if crossing midnight). There are five sample period types, three
    of which are specified by the user (morning rush, evening rush, weekend), and two
    of which are inferred by filling in the gaps between those (mid day, night).
    """

    class SamplePeriodTypes(object):
        MORNING = 'morning'
        MIDDAY = 'midday'
        EVENING = 'evening'
        NIGHT = 'night'
        WEEKEND = 'weekend'
        CHOICES = (
            (MORNING, 'Morning Rush'),
            (MIDDAY, 'Mid Day'),
            (EVENING, 'Evening Rush'),
            (NIGHT, 'Night'),
            (WEEKEND, 'Weekend'),
        )
    type = models.CharField(max_length=7, choices=SamplePeriodTypes.CHOICES, unique=True)

    # Starting datetime of sample
    period_start = models.DateTimeField()

    # Ending datetime of sample
    period_end = models.DateTimeField()


class Indicator(models.Model):
    """Stores a single indicator calculation"""

    class AggregationTypes(object):
        ROUTE = 'route'
        MODE = 'mode'
        SYSTEM = 'system'
        CHOICES = (
            (ROUTE, 'Route'),
            (MODE, 'Mode'),
            (SYSTEM, 'System'),
        )

    class IndicatorTypes(object):
        ACCESS_INDEX = 'access_index'
        AFFORDABILITY = 'affordability'
        AVG_SERVICE_FREQ = 'avg_service_freq'
        COVERAGE = 'coverage'
        COVERAGE_STOPS = 'coverage_stops'
        DISTANCE_STOPS = 'distance_stops'
        DWELL_TIME = 'dwell_time'
        HOURS_SERVICE = 'hours_service'
        JOB_ACCESS = 'job_access'
        LENGTH = 'length'
        LINES_ROADS = 'lines_roads'
        LINE_NETWORK_DENSITY = 'line_network_density'
        NUM_MODES = 'num_modes'
        NUM_ROUTES = 'num_routes'
        NUM_STOPS = 'num_stops'
        NUM_TYPES = 'num_types'
        ON_TIME_PERF = 'on_time_perf'
        REGULARITY_HEADWAYS = 'regularity_headways'
        SERVICE_FREQ_WEIGHTED = 'service_freq_weighted'
        STOPS_ROUTE_LENGTH = 'stops_route_length'
        SUBURBAN_LINES = 'suburban_lines'
        SYSTEM_ACCESS = 'system_access'
        SYSTEM_ACCESS_LOW = 'system_access_low'
        TIME_TRAVELED_STOPS = 'time_traveled_stops'
        TRAVEL_TIME = 'travel_time'
        WEEKDAY_END_FREQ = 'weekday_end_freq'
        CHOICES = (
            (ACCESS_INDEX, 'Access index'),
            (AFFORDABILITY, 'Affordability'),
            (AVG_SERVICE_FREQ, 'Average Service Frequency'),
            (COVERAGE, 'System coverage'),
            (COVERAGE_STOPS, 'Coverage of transit stops'),
            (DISTANCE_STOPS, 'Distance between stops'),
            (DWELL_TIME, 'Dwell Time Performance'),
            (HOURS_SERVICE, 'Weekly number of hours of service'),
            (JOB_ACCESS, 'Job accessibility'),
            (LENGTH, 'Transit system length'),
            (LINES_ROADS, 'Ratio of transit lines length over road length'),
            (LINE_NETWORK_DENSITY, 'Transit line network density'),
            (NUM_MODES, 'Number of modes'),
            (NUM_ROUTES, 'Number of routes'),
            (NUM_STOPS, 'Number of stops'),
            (NUM_TYPES, 'Number of route types'),
            (ON_TIME_PERF, 'On-Time Performance'),
            (REGULARITY_HEADWAYS, 'Regularity of Headways'),
            (SERVICE_FREQ_WEIGHTED, 'Service frequency weighted by served population'),
            (STOPS_ROUTE_LENGTH, 'Ratio of number of stops to route-length'),
            (SUBURBAN_LINES, 'Ratio of the Transit-Pattern Operating Suburban Lines'),
            (SYSTEM_ACCESS, 'System accessibility'),
            (SYSTEM_ACCESS_LOW, 'System accessibility - low-income'),
            (TIME_TRAVELED_STOPS, 'Time traveled between stops'),
            (TRAVEL_TIME, 'Travel Time Performance'),
            (WEEKDAY_END_FREQ, 'Weekday / weekend frequency'),
        )

    # Slice of time used for calculating this indicator
    sample_period = models.ForeignKey(SamplePeriod)

    # Type of indicator
    type = models.CharField(max_length=32, choices=IndicatorTypes.CHOICES)

    # Level in which this indicator is aggregated
    aggregation = models.CharField(max_length=6, choices=AggregationTypes.CHOICES)

    # Reference to the route id. Only relevant for a ROUTE aggregation type.
    # This is the route_id column found within the GTFS routes table.
    route_id = models.CharField(max_length=32, null=True)

    # Reference to the route type id. Only relevant for a MODE aggregation type.
    # This is the route_type column found within the GTFS routes table.
    route_type = models.PositiveIntegerField(null=True)

    # Whether or not this calculation is contained within the defined city boundaries
    city_bounded = models.BooleanField(default=False)

    # Version of data this indicator was calculated against. For the moment, this field
    # is a placeholder. The versioning logic still needs to be solidified -- e.g. versions
    # will need to be added to the the GTFS (and other data) rows.
    version = models.PositiveIntegerField(default=0)

    # Numerical value of the indicator calculation
    value = models.FloatField(default=0)
