# TreasureHuntService
The TreasureHuntService handles easter egg discoveries in the lobby.

##Endpoints
###/players/{uuid}/treasures/{treasureId} [PUT]:
####Adds the treasureId to the found treasures, and returns whether or not it was already found

**Arguments**:
- uuid (string): The unique id of the player
- treasureId (string): The treasure identifier

**Response**: {"success": true}
- success (boolean): Whether or not the treasure was added to the found treasures, when this is false it usually means the treasure was already discovered.
- err (string)[OPTIONAL]: When an unexpected error occured (fe. database not accessable), this field contains the error message. This will never be added if success=true

###/players/{uuid}/treasures/ [GET]:
####Gets the treasures that the player has found

**Arguments**:
- uuid (string): The unique id of the player

**Response**: {"count": 2, "treasures": ["treasureId1", "treasureId2"]}
- count (int): The amount of found treasures
- treasures (array of strings): The found treasures by id
