
cmake_minimum_required (VERSION 2.6)

# to see the compiler output
set(CMAKE_VERBOSE_MAKEFILE ON)

project (${projectName})


set( SOURCES main.cpp  )
             
set( HEADERS           )


## any additional defines go here 
# add_definitions(-D__STDC_CONSTANT_MACROS)

## the include search path goes here, add any additional include directories here
include_directories( ${r"${SOURCE_DIR}"} )

##  the linker search path goes here, add any additional library directories here
# link_directories(  )

add_executable( ${projectName} ${r"${SOURCES}"} ${r"${HEADERS}"})

## to link against external librarirs, list them here:
# target_link_libraries( ${projectName} m )    # link against libm
	  
					  
install(TARGETS  ${projectName}  DESTINATION  /bin )

