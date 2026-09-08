## 2024-05-14 - Accessibility Labels for Icon-only Buttons
**Learning:** Icon-only buttons (like those inside search fields or delete actions) often lack content descriptions by default when using `contentDescription = null`, causing screen readers to skip them entirely.
**Action:** Always ensure `IconButton` elements containing `Icon` components have descriptive, context-aware `contentDescription` properties to make them accessible to screen readers, instead of relying on `null`.
