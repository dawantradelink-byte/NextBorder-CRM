
## 2024-05-25 - Missing ARIA Labels on IconButtons
**Learning:** Found a pattern where `contentDescription = null` was passed to `Icon` elements inside `IconButton`s, particularly for search "Clear" and "Delete" actions across various panels (like `DashboardScreen` and `ExecutiveMemoryTimelinePanel`). This is a critical accessibility issue, as screen readers rely entirely on `contentDescription` for icon-only buttons.
**Action:** Always ensure descriptive `contentDescription` strings are provided for `Icon`s inside `IconButton`s to maintain proper accessibility for visually impaired users.
