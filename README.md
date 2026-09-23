# Employee Cab Pooling & Smart Pickup Routing

A Spring Boot backend application for employee cab pooling and pickup route optimization.

## Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- MySQL
- Spring Security
- JUnit 5
- Maven

## Architecture

The application follows a simple layered architecture:

Controller → Service → Repository → MySQL

- Controller: Handles REST API requests.
- Service: Contains business logic such as booking, grouping and route optimization.
- Repository: Handles database operations using Spring Data JPA.
- Entity: Represents database tables.

## Main Features

### Employee Management
- Create employees.
- Retrieve employees.
- Store employee location and gender.

### Booking Management
- Create employee cab bookings.
- Prevent duplicate booking for the same employee and shift.
- Cancel existing bookings.

### Cab Pooling

Employees are grouped based on:

- Office
- Shift time
- Cab capacity
- Geographic proximity

The system supports cab capacities such as 4 or 6 seats.

### Route Optimization

The system uses a simple nearest-neighbour heuristic to generate pickup routes.

The route starts from the employee group and repeatedly selects the nearest unvisited employee.

The final route continues to the office.

### Pickup ETA

Pickup ETA is estimated using:

Distance / Average Speed

The current implementation assumes an average speed of 30 km/h.

### Maximum Ride Time

A route is rejected if the calculated ride time of any employee exceeds the configured maximum ride time.

### Detour Validation

The system compares the route distance with the employee's direct distance to the office.

A route is rejected when the detour exceeds the configured threshold.

### Night Safety

For night shifts:

- A female employee cannot be the first pickup.
- A female employee cannot be the last drop.

The system rejects a route when these safety rules are violated.

### Authentication

Spring Security with HTTP Basic Authentication is used.

Roles:

- ADMIN
- EMPLOYEE

Route generation is restricted to ADMIN users.

Employee and booking APIs are available to ADMIN and EMPLOYEE users.

### Error Handling

A global exception handler returns readable JSON error responses instead of exposing raw server errors.

Example:

{
"error": "Employee already has a booking for this shift"
}

### Monitoring

Spring Boot Actuator is used for application health monitoring.

Endpoint:

`GET /actuator/health`

The health endpoint checks application components such as database connectivity.

## Algorithm

### Employee Grouping

Confirmed bookings are processed and grouped until the cab capacity is reached.

### Route Optimization

A nearest-neighbour approach is used.

For each employee:

1. Select the current employee.
2. Find the nearest unvisited employee.
3. Add that employee to the route.
4. Continue until all employees are visited.
5. Calculate the route back to the office.

This is a heuristic approach and does not guarantee the globally optimal route.

## Complexity

For a cab containing `n` employees:

### Nearest-Neighbour Route

Time complexity:

`O(n²)`

Space complexity:

`O(n)`

### Why not brute force?

Trying every possible pickup order has factorial complexity:

`O(n!)`

This becomes impractical as the number of employees increases.

Therefore, the current implementation uses a simpler nearest-neighbour heuristic suitable for small cab groups.

## Distance Calculation

The application currently uses the Haversine formula to calculate straight-line geographic distance from latitude and longitude.

This is not actual road distance.

A production system could replace this with a road-routing service such as a maps/routing API.

## Assumptions

- Average vehicle speed is assumed to be 30 km/h.
- Latitude and longitude are available for employees.
- Office latitude and longitude are provided while generating routes.
- Current distance calculations use straight-line distance.
- Maximum ride time is configurable.
- Cab capacity is configurable.
- Night safety rules are treated as hard constraints.

## Trade-offs

### Nearest-Neighbour vs Exact Optimization

Nearest-neighbour is much simpler and faster to implement than trying every possible route.

The trade-off is that it may not always produce the globally shortest route.

### Straight-Line Distance vs Road Distance

Straight-line distance is easy to calculate without an external service.

The trade-off is that it does not account for roads, traffic or actual travel time.

### In-Memory Authentication vs Database Authentication

The current project uses in-memory users because this is a case-study backend.

A production system would store users securely in a database and use a more complete authentication flow.

## Failure Handling

The application rejects:

- Unknown employee IDs.
- Duplicate employee bookings for the same shift.
- Cancellation of an unknown booking.
- Cancellation of an already cancelled booking.
- Routes exceeding maximum ride time.
- Routes exceeding the detour threshold.
- Routes violating night safety rules.
- Route generation when no confirmed bookings exist.

## Testing

The project contains JUnit tests for:

- Spring Boot application context.
- Distance calculation.
- Same-location distance.

Current test result:

`3 tests passed, 0 failures, 0 errors`

## Example APIs

### Create Employee

`POST /api/employees`

### Get Employees

`GET /api/employees`

### Create Booking

`POST /api/bookings`

### Cancel Booking

`PUT /api/bookings/{bookingId}/cancel`

### Generate Routes

`POST /api/routes/generate`

### Health Check

`GET /actuator/health`

## Future Improvements

For a production-scale system, the following could be added:

- Actual road-distance and traffic APIs.
- Spatial indexing such as geohash/grid-based employee lookup.
- Redis caching.
- Better route optimization such as 2-opt.
- Database-backed authentication.
- Automatic route re-planning after cancellation/no-show.
- Late-booking insertion into an existing route.
- Real-time vehicle tracking.