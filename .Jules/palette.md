## 2026-09-09 - Accessibility metadata anti-pattern for decorative icons
**Learning:** Adding `contentDescription` to decorative icons that accompany text (like "Notes" icon next to "Meeting Notes: ...") is an accessibility anti-pattern. Screen readers will announce the description and then immediately repeat the same information from the text, creating redundancy.
**Action:** Always verify if an `Icon` in Jetpack Compose is standalone (like an `IconButton`) or purely decorative. Purely decorative icons adjacent to descriptive text should explicitly keep `contentDescription = null`.
