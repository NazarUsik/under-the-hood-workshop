# Contributing

Thanks for your interest in improving this workshop! Whether you're fixing a typo, improving an explanation, or adding
an exercise, all contributions are welcome.

## How to Contribute

1. **Open an issue first.** Describe what you'd like to change. This avoids duplicate work and lets us discuss the
   approach.
2. **Fork the repo** and create a branch from `main`.
3. **Make your changes.** Follow the guidelines below.
4. **Submit a pull request** using the PR template.

## Guidelines

### Sessions are self-contained

Each session folder is independent. Your changes should not create dependencies between sessions. If you modify
`session-03`, make sure `session-01` and `session-04` still work without your change.

### Code style

- DDD structure: model, repository, service, controller/handler
- Readability over cleverness
- Comments explain "why", not "what"
- Each project must be runnable with a single command documented in its README

### Content style

- Conversational tone, no jargon without explanation
- Terminology spelled out on first use
- No em dashes; use colons, semicolons, or regular dashes instead
- README-driven: all content lives in Markdown files, not slides

### Languages and frameworks

| Language   | Framework    | Build Tool |
|------------|--------------|------------|
| Java       | Spring Boot  | Maven      |
| Python     | FastAPI      | pip        |
| TypeScript | NestJS       | npm        |
| Go         | Gin / stdlib | Go modules |

If you want to add an example in a new language, open an issue to discuss it first.

### Diagrams

- Source files: `.drawio` format in `diagrams/` folders
- Exports: `.drawio.svg` for animated diagrams, `.drawio.png` for static
- Keep both source and export files
- See `.claude/rules/drawio-conventions.md` for full style guide

### Testing your changes

- Run the project locally before submitting
- If the session has tests (Sessions 5-6), make sure they pass
- Verify the README instructions are accurate

## What to Contribute

Here are some ideas:

- **Fix a bug** in a code example
- **Improve an explanation** that's unclear
- **Add a "Common Issues" section** to a language README
- **Add "Further Reading" links** to a session README
- **Improve exercises** or add stretch goals
- **Add a new language** example (open an issue first)
- **Fix typos or formatting**

## Questions?

Open a [Discussion](https://github.com/NazarUsik/under-the-hood-workshop/discussions) or file an issue with the
`question` label.
