# Vidtrain

Setup instructions: 

Clone this repository and open the root directory in Android Studio. Then run on any Android device or simulator.

## About the app

RideCellParking is a simple app built off of the RideCell parking locations API: http://ridecellparking.herokuapp.com/api/v1/parkinglocations

Users can view potential parking spots in San Francisco, whether or not they're available, as well as reserve them for a particular time period.

* User Stories
	*  :white_check_mark: View all available street parking spots on a map.
	*  :white_check_mark: Select a parking spot to see more information about the spot
		*  :white_check_mark: Spot name
		*  :white_check_mark: Spot number
		*  :white_check_mark: Per minute charge
	*  :white_check_mark: Reserve a parking spot.
	*  :white_check_mark: Option to extend reservation after the initial reservation time elapse.
	*  :white_check_mark: See a summary of the reservation at the end of the reservation duration.

Note that on the story to extend a reservation after the intiial reservation time elapses. The way this is implemented is that you can simply redo the search over the current parking location and then make a new reservation.

![Core Functionality](https://github.com/franklinho/RideCellParking/blob/master/RideCellWalkThrough.gif)

